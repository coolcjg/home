package com.cjg.home.repository;


import com.cjg.home.domain.Post;
import com.cjg.home.dto.request.PostListRequestDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.cjg.home.domain.QPost.post;


@Repository
@AllArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Post> list(Pageable pageable, PostListRequestDto dto) {

        JPAQuery<Post> query =  jpaQueryFactory
                .selectFrom(post)
                .where(
                        containsTitle(dto.getTitle())
                        ,containsContent(dto.getContent())
                        ,eqUserId(dto.getUserId())
                        ,eqOpen()
                );

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(
                        containsTitle(dto.getTitle())
                        ,containsContent(dto.getContent())
                        ,eqUserId(dto.getUserId())
                        ,eqOpen()
                );

        List<Post> list = query.orderBy(post.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(list , pageable, countQuery::fetchOne);

    }

    private BooleanExpression containsTitle(String title){
        if(!StringUtils.hasText(title)) return null;
        return post.title.contains(title);
    }

    private BooleanExpression containsContent(String content){
        if(!StringUtils.hasText(content)) return null;
        return post.content.contains(content);
    }

    private BooleanExpression eqUserId(String userId){
        if(!StringUtils.hasText(userId)) return null;
        return post.user.userId.eq(userId);
    }

    private BooleanExpression eqOpen(){
        return post.open.eq('Y');
    }

}
