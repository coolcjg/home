package com.cjg.home.repository;

import com.cjg.home.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Long countByUserId(String userId);

    User findByUserId(String userId);

}
