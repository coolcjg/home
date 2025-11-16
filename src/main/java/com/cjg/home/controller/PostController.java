package com.cjg.home.controller;


import com.cjg.home.code.ResultCode;
import com.cjg.home.domain.CustomUserDetails;
import com.cjg.home.dto.request.PostDeleteRequestDto;
import com.cjg.home.dto.request.PostModifyRequestDto;
import com.cjg.home.dto.request.PostSaveRequestDto;
import com.cjg.home.dto.response.PostResponseDto;
import com.cjg.home.response.Response;
import com.cjg.home.service.PostService;
import com.cjg.home.util.AuthCheck;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final AuthCheck auth;

    @GetMapping(value = "/v1/post/temp/{userId}")
    public ResponseEntity<Response<PostResponseDto>> loadTemp( @PathVariable String userId){
        return ResponseEntity.ok(Response.success(ResultCode.POST_TEMP_SELECT_SUCCESS, postService.loadTemp(userId)));
    }

    @PostMapping(value = "/v1/post/temp")
    public ResponseEntity<Response<Void>> saveTemp(@RequestBody @Valid PostSaveRequestDto dto){
        postService.saveTemp(dto);
        return ResponseEntity.ok(Response.success(ResultCode.POST_TEMP_SAVE_SUCCESS));
    }
    
    @PostMapping(value = "/v1/post")
    public ResponseEntity<Response<PostResponseDto>> save(@RequestBody @Valid PostSaveRequestDto dto){
        return ResponseEntity.ok(Response.success(ResultCode.POST_SAVE_SUCCESS, postService.save(dto)));
    }

    @PutMapping(value = "/v1/post")
    public ResponseEntity<Response<?>> modify(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid PostModifyRequestDto dto){
        if(auth.isSameUserForPost(customUserDetails, dto.getPostId())){
            return ResponseEntity.ok(Response.success(ResultCode.POST_MODIFY_SUCCESS, postService.modify(dto)));
        }else{
            return ResponseEntity.status(ResultCode.POST_INVALID_AUTH.getHttpStatus()).body(Response.fail(ResultCode.POST_INVALID_AUTH));
        }
    }

    @DeleteMapping(value = "/v1/post")
    public ResponseEntity<Response<Void>> delete(@RequestBody @Valid PostDeleteRequestDto dto){
        postService.delete(dto);
        return ResponseEntity.ok(Response.success(ResultCode.POST_DELETE_SUCCESS));
    }

}
