package com.cjg.home.controller;

import com.cjg.home.code.ResultCode;
import com.cjg.home.domain.CustomUserDetails;
import com.cjg.home.dto.request.PostListRequestDto;
import com.cjg.home.exception.CustomViewException;
import com.cjg.home.service.PostService;
import com.cjg.home.service.SubscribeService;
import com.cjg.home.util.AuthCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@Log4j2
public class PostViewController {

    private final PostService postService;
    private final AuthCheck authCheck;
    private final SubscribeService subscribeService;

    @GetMapping(value = "/")
    public RedirectView home(){
        return new RedirectView("/post/list");
    }

    @GetMapping(value = "/post/list")
    public String list(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model
            ,@RequestParam(required = false) String searchType
            ,@RequestParam(required = false) String searchText
            ,@RequestParam(required = false, defaultValue = "1") Integer pageNumber
            ,@RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        PostListRequestDto dto = PostListRequestDto.builder()
                .searchType(searchType)
                .searchText(searchText)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        dto.checkParam();

        model.addAttribute("data", postService.list(dto));
        return "post/list";
    }

    @GetMapping(value = "/post/write")
    public String write(){
        return "post/write";
    }

    @GetMapping(value = "/post/{postId}")
    public String view(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long postId, Model model){
        model.addAttribute("data", postService.view(customUserDetails, postId));

        if(customUserDetails != null){
            model.addAttribute("subscribeStatus", postService.subscribeStatus(customUserDetails, postId));
        }
        return "post/view";
    }

    @GetMapping(value = "/post/{postId}/modify")
    public String modify(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long postId, Model model){
        if(authCheck.isSameUserForPost(customUserDetails, postId)){
            model.addAttribute("data", postService.view(customUserDetails, postId));
        }else{
            throw new CustomViewException(ResultCode.POST_INVALID_AUTH);
        }
        return "post/modify";
    }

}
