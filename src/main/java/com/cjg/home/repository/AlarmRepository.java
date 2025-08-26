package com.cjg.home.repository;

import com.cjg.home.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long>, CustomAlarmRepository {
}
