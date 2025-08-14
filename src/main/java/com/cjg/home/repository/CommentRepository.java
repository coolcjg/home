package com.cjg.home.repository;


import com.cjg.home.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository  {
    void deleteByUserUserId(String userId);
    void deleteByPostPostId(Long postId);

}
