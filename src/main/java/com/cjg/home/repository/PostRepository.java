package com.cjg.home.repository;

import com.cjg.home.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {
    void deleteByUserUserId(String userId);
}
