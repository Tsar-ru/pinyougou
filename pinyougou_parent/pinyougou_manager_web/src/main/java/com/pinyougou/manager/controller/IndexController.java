package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class IndexController {

    @RequestMapping("/showUsername")
    public String showUsername(){
//        从SpringSecurity中获取当前登录名
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
