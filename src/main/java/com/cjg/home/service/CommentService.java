package com.cjg.home.service;

import com.cjg.home.code.ResultCode;
import com.cjg.home.domain.Comment;
import com.cjg.home.domain.Post;
import com.cjg.home.domain.User;
import com.cjg.home.dto.request.CommentDeleteRequestDto;
import com.cjg.home.dto.request.CommentModifyRequestDto;
import com.cjg.home.dto.request.CommentSaveRequestDto;
import com.cjg.home.dto.response.CommentResponseDto;
import com.cjg.home.exception.CustomException;
import com.cjg.home.repository.CommentRepository;
import com.cjg.home.repository.PostRepository;
import com.cjg.home.repository.UserRepository;
import com.cjg.home.util.AES256;
import com.cjg.home.util.DateToString;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final DateToString dateToString;
    private final AES256 aes256;

    @Transactional
    public CommentResponseDto save(CommentSaveRequestDto dto){

        Post post = postRepository.findById(dto.getPostId()).orElseThrow(() -> new CustomException(ResultCode.POST_SEARCH_NOT_FOUND));
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new CustomException(ResultCode.USER_SEARCH_NOT_FOUND));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .deleted('N')
                .content(dto.getContent())
                .build();

        if(dto.getParentId() != 0){
            Comment parent = commentRepository.findById(dto.getParentId()).orElseThrow(() -> new CustomException(ResultCode.COMMENT_SEARCH_NOT_FOUND));
            comment.setParent(parent);
        }

        Comment result = commentRepository.save(comment);

        if(dto.getParentId() == 0){
            result.setParent(result);
        }

        return commentToDto(result);
    }

    @Transactional
    public CommentResponseDto modify(CommentModifyRequestDto dto){
        Comment comment = commentRepository.findById(dto.getCommentId()).orElseThrow(()->new CustomException(ResultCode.COMMENT_SEARCH_NOT_FOUND));
        comment.setContent(dto.getContent());
        comment.setModDate(LocalDateTime.now());
        return commentToDto(comment);
    }

    @Transactional
    public void delete(CommentDeleteRequestDto dto){
        Comment comment = commentRepository.findById(dto.getCommentId()).orElseThrow(()->new CustomException(ResultCode.COMMENT_SEARCH_NOT_FOUND));
        comment.setDeleted('Y');
    }

    private CommentResponseDto commentToDto(Comment comment){
        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .parentId(comment.getParent() != null ? comment.getParent().getCommentId() : 0)
                .postId(comment.getPost().getPostId())
                .userId(comment.getUser().getUserId())
                .name(aes256.decrypt(comment.getUser().getName()))
                .content(comment.getContent())
                .regDate(dateToString.apply(comment.getRegDate()))
                .build();
    }
}