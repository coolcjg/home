package com.cjg.home.repository;

import com.cjg.home.domain.Alarm;
import com.cjg.home.dto.request.AlarmListRequestDto;
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

import static com.cjg.home.domain.QAlarm.alarm;

@Repository
@AllArgsConstructor
public class CustomAlarmRepositoryImpl implements CustomAlarmRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Alarm> list(Pageable pageable, AlarmListRequestDto dto) {

        JPAQuery<Alarm> query =  jpaQueryFactory
                .selectFrom(alarm)
                .where(
                    eqUserId(dto.getUserId())
                );

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(alarm.count())
                .from(alarm)
                .where(
                        eqUserId(dto.getUserId())
                );

        List<Alarm> list = query.orderBy(alarm.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(list , pageable, countQuery::fetchOne);

    }

    private BooleanExpression eqUserId(String userId){
        if(!StringUtils.hasText(userId)) return null;
        return alarm.user.userId.eq(userId);
    }
}
