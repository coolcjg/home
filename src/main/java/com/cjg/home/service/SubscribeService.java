package com.cjg.home.service;

import com.cjg.home.code.ResultCode;
import com.cjg.home.domain.Subscribe;
import com.cjg.home.domain.SubscribePrimaryKey;
import com.cjg.home.domain.User;
import com.cjg.home.dto.request.SubscribeRequestDto;
import com.cjg.home.dto.response.SubscribeResponseDto;
import com.cjg.home.exception.CustomException;
import com.cjg.home.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubscribeService {

    private final UserService userService;
    private final SubscribeRepository subscribeRepository;

    public SubscribeResponseDto save(SubscribeRequestDto dto){

        log.info("param {}", dto);

        User user = userService.findByUserId(dto.getUserId());
        User targetUser = userService.findByUserId(dto.getTargetUserId());

        Subscribe subscribe = Subscribe.builder().user(user).targetUser(targetUser).build();

        if(isSubscribe(dto)){
            throw new CustomException(ResultCode.SUBSCRIBE_SAVE_FAIL_DUPLICATED);
        }

        Subscribe result = subscribeRepository.save(subscribe);

        return SubscribeResponseDto.builder()
                .userId(result.getUser().getUserId())
                .targetUserId(result.getTargetUser().getUserId())
                .regDate(result.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build();

    }

    public boolean isSubscribe(SubscribeRequestDto dto){

        User user = userService.findByUserId(dto.getUserId());
        User targetUser = userService.findByUserId(dto.getTargetUserId());

        SubscribePrimaryKey spk = new SubscribePrimaryKey(user, targetUser);

        return subscribeRepository.findById(spk).isPresent();

    }

    public void delete(SubscribeRequestDto dto){
        log.info("param {}", dto);
        User user = userService.findByUserId(dto.getUserId());
        User targetUser = userService.findByUserId(dto.getTargetUserId());
        Subscribe subscribe = Subscribe.builder().user(user).targetUser(targetUser).build();
        subscribeRepository.delete(subscribe);
    }
}
