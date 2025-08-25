package com.cjg.home.service;

import com.cjg.home.domain.Alarm;
import com.cjg.home.domain.Subscribe;
import com.cjg.home.dto.request.AlarmSaveRequestDto;
import com.cjg.home.repository.AlarmRepository;
import com.cjg.home.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final SubscribeRepository subscribeRepository;

    public void save(AlarmSaveRequestDto dto){

        String userId = dto.getUserId();

        List<Subscribe> list = subscribeRepository.findAllByTargetUser_UserId(userId);

        list.forEach(subscribe -> {
            Alarm alarm = Alarm.builder()
                    .user(subscribe.getUser())
                    .message(dto.getMessage())
                    .link(dto.getLink())
                    .build();
            alarmRepository.save(alarm);
        });
    };
}
