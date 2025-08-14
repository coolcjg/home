package com.cjg.home.repository;

import com.cjg.home.domain.Post;
import com.cjg.home.dto.request.PostListRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPostRepository {

    Page<Post> list(Pageable pageable, PostListRequestDto dto);
}
