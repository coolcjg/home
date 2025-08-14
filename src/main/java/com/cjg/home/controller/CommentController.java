package com.cjg.home.controller;


import com.cjg.home.code.ResultCode;
import com.cjg.home.domain.CustomUserDetails;
import com.cjg.home.dto.request.CommentDeleteRequestDto;
import com.cjg.home.dto.request.CommentModifyRequestDto;
import com.cjg.home.dto.request.CommentSaveRequestDto;
import com.cjg.home.dto.response.CommentResponseDto;
import com.cjg.home.response.Response;
import com.cjg.home.service.CommentService;
import com.cjg.home.util.AuthCheck;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final AuthCheck auth;

    @PostMapping(value = "/v1/comment")
    public ResponseEntity<Response<CommentResponseDto>> save(@RequestBody @Valid CommentSaveRequestDto dto){
        return ResponseEntity.ok(Response.success(ResultCode.COMMENT_SAVE_SUCCESS, commentService.save(dto)));
    }

    @PutMapping(value = "/v1/comment")
    public ResponseEntity<Response<?>> modify(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid CommentModifyRequestDto dto){
        if(auth.isSameUserForComment(customUserDetails, dto.getCommentId())){
            return ResponseEntity.ok(Response.success(ResultCode.COMMENT_MODIFY_SUCCESS, commentService.modify(dto)));
        }else{
            return ResponseEntity.status(ResultCode.COMMENT_INVALID_AUTH.getHttpStatus()).body(Response.fail(ResultCode.COMMENT_INVALID_AUTH));
        }
    }

    @DeleteMapping(value = "/v1/comment")
    public ResponseEntity<Response<Void>> delete(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid CommentDeleteRequestDto dto){
        if(auth.isSameUserForComment(customUserDetails, dto.getCommentId())){
            commentService.delete(dto);
            return ResponseEntity.ok(Response.success(ResultCode.COMMENT_DELETE_SUCCESS));
        }else{
            return ResponseEntity.status(ResultCode.COMMENT_INVALID_AUTH.getHttpStatus()).body(Response.fail(ResultCode.COMMENT_INVALID_AUTH));
        }
    }

}
