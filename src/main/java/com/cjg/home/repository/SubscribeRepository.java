package com.cjg.home.repository;

import com.cjg.home.domain.Subscribe;
import com.cjg.home.domain.SubscribePrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, SubscribePrimaryKey> {

    List<Subscribe> findAllByTargetUser_UserId(String userId);
}
