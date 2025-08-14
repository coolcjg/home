package com.cjg.home.util;

import com.cjg.home.code.ResultCode;
import com.cjg.home.domain.Comment;
import com.cjg.home.domain.CustomUserDetails;
import com.cjg.home.domain.Post;
import com.cjg.home.domain.User;
import com.cjg.home.exception.CustomException;
import com.cjg.home.repository.CommentRepository;
import com.cjg.home.repository.PostRepository;
import com.cjg.home.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AuthCheck {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public boolean isSameUserForUser(CustomUserDetails customUserDetails, String userId){
        boolean result = false;
        User user = userRepository.findById(userId).orElseThrow(()-> new CustomException(ResultCode.USER_SEARCH_NOT_FOUND));

        if(customUserDetails != null && customUserDetails.getUsername().equals(user.getUserId())){
            result = true;
        }
        return result;
    }

    public boolean isSameUserForPost(CustomUserDetails customUserDetails, Long postId){
        boolean result = false;
        Post post = postRepository.findById(postId).orElseThrow(()-> new CustomException(ResultCode.POST_SEARCH_NOT_FOUND));

        if(customUserDetails != null && customUserDetails.getUsername().equals(post.getUser().getUserId())){
            result = true;
        }
        return result;
    }

    public boolean isSameUserForComment(CustomUserDetails customUserDetails, Long id){
        boolean result = false;
        Comment comment = commentRepository.findById(id).orElseThrow(()-> new CustomException(ResultCode.COMMENT_SEARCH_NOT_FOUND));

        if(customUserDetails != null && customUserDetails.getUsername().equals(comment.getUser().getUserId())){
            result = true;
        }
        return result;
    }
}
