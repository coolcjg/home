package com.cjg.home.repository;

import com.cjg.home.domain.Alarm;
import com.cjg.home.dto.request.AlarmListRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomAlarmRepository {
    Page<Alarm> list(Pageable pageable, AlarmListRequestDto dto);
}
