package com.cjg.home.controller;

import com.cjg.home.code.ResultCode;
import com.cjg.home.dto.request.SubscribeRequestDto;
import com.cjg.home.dto.response.SubscribeResponseDto;
import com.cjg.home.response.Response;
import com.cjg.home.service.SubscribeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeService subscribeService;

    @PostMapping(value = "/subscribe")
    public ResponseEntity<Response<SubscribeResponseDto>> save(@RequestBody @Valid SubscribeRequestDto dto){
        return ResponseEntity.ok(Response.success(ResultCode.SUBSCRIBE_SAVE_SUCCESS, subscribeService.save(dto)));
    }

    @DeleteMapping(value = "/subscribe")
    public ResponseEntity<Response<Void>> delete(@RequestBody @Valid SubscribeRequestDto dto){
        subscribeService.delete(dto);
        return ResponseEntity.ok(Response.success(ResultCode.SUBSCRIBE_DELETE_SUCCESS));
    }
}
