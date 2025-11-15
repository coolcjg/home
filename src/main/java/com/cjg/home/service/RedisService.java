package com.cjg.home.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Log4j2
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String key, String value, Long timeout, TimeUnit unit){
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }

    public String find(String key){
        return redisTemplate.opsForValue().get(key);
    }

}
