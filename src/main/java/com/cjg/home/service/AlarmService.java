package com.cjg.home.service;

import com.cjg.home.code.ResultCode;
import com.cjg.home.domain.Alarm;
import com.cjg.home.domain.Subscribe;
import com.cjg.home.dto.request.*;
import com.cjg.home.dto.response.AlarmCountResponseDto;
import com.cjg.home.dto.response.AlarmListResponseDto;
import com.cjg.home.dto.response.AlarmResponseDto;
import com.cjg.home.exception.CustomException;
import com.cjg.home.repository.AlarmRepository;
import com.cjg.home.repository.SubscribeRepository;
import com.cjg.home.util.DateToString;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final SubscribeRepository subscribeRepository;
    private final DateToString dateToString;

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


    public AlarmListResponseDto list(AlarmListRequestDto dto){

        Pageable pageable = PageRequest.of(dto.getPageNumber()-1, dto.getPageSize(), Sort.Direction.DESC, "regDate");
        Page<Alarm> page =  alarmRepository.list(pageable, dto);
        int totalPage = page.getTotalPages() == 0 ? 1 : page.getTotalPages();

        List<AlarmResponseDto> list = new ArrayList<>();
        for(Alarm element : page.getContent()) {
            AlarmResponseDto temp = AlarmResponseDto.builder()
                    .alarmId(element.getAlarmId())
                    .checked(element.getChecked())
                    .link(element.getLink())
                    .message(element.getMessage())
                    .userId(element.getUser().getUserId())
                    .regDate(dateToString.apply(element.getRegDate()))
                    .build();
            list.add(temp);
        }

        return AlarmListResponseDto.builder()
                .list(list)
                .pageNumber(page.getPageable().getPageNumber()+1)
                .totalPage(totalPage)
                .totalCount(page.getTotalElements())
                .build();
    }

    public AlarmCountResponseDto count(AlarmCountRequestDto dto){
        return AlarmCountResponseDto.builder()
                .count(alarmRepository.count(dto))
                .build();
    }

    public void check(AlarmCheckRequestDto dto){
        Alarm alarm = alarmRepository.findById(dto.getAlarmId()).orElseThrow(()-> new CustomException(ResultCode.ALARM_NOT_FOUND));
        alarm.setChecked("Y");
        alarmRepository.save(alarm);
    }

    public void delete(AlarmDeleteRequestDto dto){
        Alarm alarm = alarmRepository.findById(dto.getAlarmId()).orElseThrow(() -> new CustomException(ResultCode.ALARM_NOT_FOUND));
        alarm.setDelYn("Y");
        alarmRepository.save(alarm);
    }
}
