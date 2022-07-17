package com.zzw.bilibili.api;

import com.zzw.Entity.User;
import com.zzw.Entity.auth.userAuthorities;
import com.zzw.bilibili.api.support.userSupport;
import com.zzw.domain.jsonResponse;
import com.zzw.service.userAuthService;
import com.zzw.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//用户权限-api
@RestController
public class userAuthApi {

    @Autowired
    private userSupport userSupport;

    @Autowired
    private userAuthService userAuthService;

    //获取用户权限
    @GetMapping("/user-authorities")
    public jsonResponse<userAuthorities> getAuthorities(){
        Long userId = userSupport.getCurrentUserID();
        userAuthorities userAuthorities =  userAuthService.getAuthorities(userId);
        return new jsonResponse<>(userAuthorities);
    }



}
