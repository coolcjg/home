package com.cjg.home.repository;

import com.cjg.home.domain.Comment;

import java.util.List;

public interface CustomCommentRepository {

    List<Comment> recursiveList(Long postId);
}
