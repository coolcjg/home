package com.cjg.home.service;

import com.cjg.home.code.ResultCode;
import com.cjg.home.document.PostDoc;
import com.cjg.home.domain.Comment;
import com.cjg.home.domain.CustomUserDetails;
import com.cjg.home.domain.Post;
import com.cjg.home.dto.request.*;
import com.cjg.home.dto.response.CommentResponseDto;
import com.cjg.home.dto.response.PageItem;
import com.cjg.home.dto.response.PostListResponseDto;
import com.cjg.home.dto.response.PostResponseDto;
import com.cjg.home.exception.CustomException;
import com.cjg.home.exception.CustomViewException;
import com.cjg.home.repository.CommentRepository;
import com.cjg.home.repository.PostDocRepository;
import com.cjg.home.repository.PostRepository;
import com.cjg.home.util.AES256;
import com.cjg.home.util.AuthCheck;
import com.cjg.home.util.DateToString;
import com.cjg.home.util.PageUtil;
import com.cjg.home.util.kafka.KafkaProducer;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class PostService {

    private final UserService userService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final SubscribeService subscribeService;
    private final DateToString dateToString;
    private final KafkaProducer kafkaProducer;
    private final AES256 aes256;
    private final AuthCheck auth;

    private final PostDocRepository postDocRepository;

    @Value("${image.url.prefix}")
    private String imageUrlPrefix;

    @Value("${topic.name}")
    private String topicName;

    @Value("${home.domain}")
    private String homeDomain;

    public PostListResponseDto list(PostListRequestDto dto){
        Pageable pageable = PageRequest.of(dto.getPageNumber()-1, dto.getPageSize(), Sort.Direction.DESC, "regDate");
        Page<Post> page =  postRepository.list(pageable, dto);

        List<PostResponseDto> list = new ArrayList<>();
        for(Post post : page.getContent()) {
            PostResponseDto temp = PostResponseDto.builder()
                    .postId(post.getPostId())
                    .userId(post.getUser().getUserId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .open(post.getOpen())
                    .viewCnt(post.getViewCnt())
                    .regDate(dateToString.apply(post.getRegDate()))
                    .modDate(dateToString.apply(post.getModDate()))
                    .build();
            list.add(temp);
        }

        int totalPage = page.getTotalPages() == 0 ? 1 : page.getTotalPages();

        String prevPage = dto.getPageNumber() > 1 ? getQueryParams(dto, dto.getPageNumber()-1) : "";
        String nextPage = dto.getPageNumber() < totalPage ? getQueryParams(dto, dto.getPageNumber()+1) : "";

        List<Integer> pagination = PageUtil.getStartEndPage(dto.getPageNumber(), totalPage);
        List<PageItem> pageItemList = pagination.stream().map(pageNumber-> new PageItem(pageNumber, getQueryParams(dto, pageNumber))).toList();

        return PostListResponseDto.builder()
                .list(list)
                .pageList(pageItemList)
                .prevPage(prevPage)
                .nextPage(nextPage)
                .pageNumber(page.getPageable().getPageNumber()+1)
                .totalPage(totalPage)
                .totalCount(page.getTotalElements())
                .searchType(dto.getSearchType())
                .searchText(dto.getSearchText())
                .build();
    }

    public String getQueryParams(PostListRequestDto dto, int pageNumber){

        StringBuilder sb = new StringBuilder();
        sb.append("/post/list?");

        if(dto.getSearchType() != null){
            sb.append("searchType=").append(dto.getSearchType()).append("&");
        }

        if(dto.getSearchText() != null){
            sb.append("searchText=").append(dto.getSearchText()).append("&");
        }

        sb.append("pageNumber=").append(pageNumber).append("&");
        sb.append("pageSize=").append(dto.getPageSize()).append("&");

        if(sb.lastIndexOf("&") == sb.length()-1){
            sb.delete(sb.length()-1, sb.length());
        }

        return sb.toString();

    };

    public void saveTemp(PostSaveRequestDto dto){

        //존재하는 id인지 체크
        String userId = userService.findByUserId(dto.getUserId()).getUserId();
        String title = dto.getTitle();
        String content = dto.getContent();
        char open = dto.getOpen().charAt(0);

        PostDoc postDoc = PostDoc.builder().userId(userId).title(title).content(content).open(open).build();

        postDocRepository.save(postDoc);
    }

    public PostResponseDto loadTemp(String userId){
        PostDoc postDoc = postDocRepository.findFirstByUserIdOrderByIdDesc(userId);
        return PostResponseDto.builder().title(postDoc.title()).content(postDoc.content()).open(postDoc.open()).build();
    }

    public PostResponseDto save(PostSaveRequestDto dto){

        Post post = Post.builder()
                .user(userService.findByUserId(dto.getUserId()))
                .title(dto.getTitle())
                .content(dto.getContent())
                .open(dto.getOpen().charAt(0))
                .viewCnt(0)
                .build();

        Post result = postRepository.save(post);

        PostResponseDto response = PostResponseDto.builder()
                .postId(result.getPostId())
                .userId(result.getUser().getUserId())
                .title(result.getTitle())
                .content(result.getContent())
                .viewCnt(result.getViewCnt())
                .open(result.getOpen())
                .regDate(dateToString.apply(result.getRegDate()))
                .modDate(dateToString.apply(result.getModDate()))
                .build();

        KafkaAlarmDto kafkaAlarmDto = KafkaAlarmDto.builder()
                .userId(result.getUser().getUserId())
                .message(result.getUser().getUserId() + "님이 새로운 글을 등록했습니다")
                .link(homeDomain +"/post/" + result.getPostId()).build();

        kafkaProducer.create(topicName, new Gson().toJson(kafkaAlarmDto));

        postDocRepository.deleteByUserId(result.getUser().getUserId());

        return response;
    }

    @Transactional
    public PostResponseDto view(CustomUserDetails customUserDetails, Long postId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new CustomViewException(ResultCode.POST_SEARCH_NOT_FOUND));
        if(post.getOpen() == 'Y'){
            post.setViewCnt(post.getViewCnt()+1);
            return postToDto(post);
        }else{
            if(customUserDetails == null){
                throw new CustomViewException(ResultCode.POST_INVALID_AUTH);
            }else{
                if(auth.isSameUserForUser(customUserDetails, post.getUser().getUserId())){
                    post.setViewCnt(post.getViewCnt()+1);
                    return postToDto(post);
                }else{
                    throw new CustomViewException(ResultCode.POST_INVALID_AUTH);
                }
            }
        }
    }

    public boolean subscribeStatus(CustomUserDetails customUserDetails, Long postId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new CustomViewException(ResultCode.POST_SEARCH_NOT_FOUND));

        SubscribeRequestDto ssrd = SubscribeRequestDto.builder()
                .userId(customUserDetails.getUsername())
                .targetUserId(post.getUser().getUserId()).build();

        return  subscribeService.isSubscribe(ssrd);
    }

    @Transactional
    public PostResponseDto modify(PostModifyRequestDto dto){

        Post post = postRepository.findById(dto.getPostId()).orElseThrow(()->new CustomException(ResultCode.POST_SEARCH_NOT_FOUND));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setOpen(dto.getOpen().charAt(0));
        post.setModDate(LocalDateTime.now());

        return postToDto(post);
    }

    public PostResponseDto postToDto(Post post){
        return PostResponseDto.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getUserId())
                .name(aes256.decrypt(post.getUser().getName()))
                .image(imageUrlPrefix + post.getUser().getImage())
                .title(post.getTitle())
                .content(post.getContent())
                .open(post.getOpen())
                .viewCnt(post.getViewCnt())
                .commentResponseDtoList(commentListToDto(commentRepository.recursiveList(post.getPostId())))
                .regDate(dateToString.apply(post.getRegDate()))
                .modDate(dateToString.apply(post.getModDate()))
                .build();
    }

    public List<CommentResponseDto> commentListToDto(List<Comment> commentList){
        return commentList.stream().map(e->CommentResponseDto.builder()
                .commentId(e.getCommentId())
                .parentId(e.getParent() != null ? e.getParent().getCommentId() : 0)
                .postId(e.getPost().getPostId())
                .userId(e.getUser().getUserId())
                .name(aes256.decrypt(e.getUser().getName()))
                .content(e.getContent())
                .deleted(e.getDeleted())
                .regDate(dateToString.apply(e.getRegDate()))
                .build()).toList();
    }

    @Transactional
    public void delete(PostDeleteRequestDto dto){
        for(Long postId : dto.getPostIds()){
            commentRepository.deleteByPostPostId(postId);
            postRepository.deleteById(postId);
        }
    }
}

