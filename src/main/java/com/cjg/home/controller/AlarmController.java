package com.cjg.home.controller;

import com.cjg.home.code.ResultCode;
import com.cjg.home.domain.CustomUserDetails;
import com.cjg.home.dto.request.AlarmDeleteRequestDto;
import com.cjg.home.dto.request.AlarmListRequestDto;
import com.cjg.home.dto.response.AlarmListResponseDto;
import com.cjg.home.response.Response;
import com.cjg.home.service.AlarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping(value = "/alarm")
    public ResponseEntity<Response<AlarmListResponseDto>> list(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid AlarmListRequestDto dto){
        return ResponseEntity.ok(Response.success(ResultCode.ALARM_LIST_SUCCESS, alarmService.list(dto)));
    }

    @DeleteMapping(value = "/alarm")
    public ResponseEntity<Response<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid AlarmDeleteRequestDto dto){
        alarmService.delete(dto);
        return ResponseEntity.ok(Response.success(ResultCode.ALARM_DELETE_SUCCESS));
    }



}
