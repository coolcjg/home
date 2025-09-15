package com.cjg.home.repository;

import com.cjg.home.document.PostDoc;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostDocRepository extends CrudRepository<PostDoc,Integer> {

    PostDoc findFirstByUserIdOrderByIdDesc(String userId);

    void deleteByUserId(String userId);

}