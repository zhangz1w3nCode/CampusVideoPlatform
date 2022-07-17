package com.zzw.bilibili.api;

import com.alibaba.fastjson.JSONObject;
import com.zzw.Entity.PageResult;
import com.zzw.Entity.User;
import com.zzw.Entity.UserInfo;
import com.zzw.bilibili.api.support.userSupport;
import com.zzw.domain.jsonResponse;
import com.zzw.service.userFollowingService;
import com.zzw.service.userService;
import com.zzw.service.utils.RSAUtil;
import com.zzw.service.utils.fastDFSUtil;
import com.zzw.service.utils.rocketMQUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class testApi {
    @Autowired
    private userService userService;

    @Autowired
    private fastDFSUtil fastDFSUtil;



    //登陆成功后 访问/user 会验证token 以及获得 获得用户信息 user
    @PostMapping("/slices")
    public void slices(MultipartFile file) throws  Exception{
        System.out.println("???");
        System.out.println(file);
        fastDFSUtil.convertFileToSlices(file);
    }






}
