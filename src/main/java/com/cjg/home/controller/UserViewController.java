package com.cjg.home.controller;

import com.cjg.home.domain.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    @GetMapping(value = "/user/login")
    public String login(){
        return "user/login";
    }

    @GetMapping(value = "/user/signup")
    public String signup(){
        return "user/signup";
    }

    @GetMapping(value = "/user/modify")
    public String modify(){
        return "user/modify";
    }

    @GetMapping(value = "/user/view")
    public String view(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return "user/view";
    }
}
