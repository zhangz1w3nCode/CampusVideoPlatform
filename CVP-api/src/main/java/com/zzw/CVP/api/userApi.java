package com.zzw.bilibili.api;

import com.alibaba.fastjson.JSONObject;
import com.zzw.Entity.PageResult;
import com.zzw.Entity.User;
import com.zzw.Entity.UserInfo;
import com.zzw.bilibili.api.support.userSupport;
import com.zzw.domain.jsonResponse;
import com.zzw.service.userFollowingService;
import com.zzw.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.zzw.service.utils.RSAUtil;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin

public class userApi {
    @Autowired
    private userService userService;

    @Autowired
    private userSupport userSupport;

    @Autowired
    private userFollowingService userFollowingService;


    //登陆成功后 访问/user 会验证token 以及获得 获得用户信息 user
    @GetMapping("/users")
    public jsonResponse<User> getUserInfo(){
        Long userID = userSupport.getCurrentUserID();
        User user = userService.getUserInfo(userID);
        return new jsonResponse<>(user);
    }

    //获取公钥方法
    @RequestMapping("/rsa-pks")
    public jsonResponse<String> getRsaPublicKey(){
        String publicKey = RSAUtil.getPublicKeyStr();
        return new jsonResponse<>(publicKey);
    }

    //用户注册方法
    @PostMapping("/users")
    public jsonResponse<String> addUser(@RequestBody User user){
        userService.addUser(user);
        return jsonResponse.success();
    }

    //用户登录方法  登陆成功 生成token
    @PostMapping("/user-tokens")
    public jsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return new jsonResponse<>(token);
    }

    //用户登录方法  登陆成功 生成token
    @PostMapping("/user-dts")
    public jsonResponse<Map<String,Object>> loginForDts(@RequestBody User user) throws Exception {
        Map<String,Object> token = userService.loginForDts(user);
        return new jsonResponse<>(token);
    }

    //用户退出-删除刷新token
    @DeleteMapping("/refresh-tokens")
    public jsonResponse<String> logOut(HttpServletRequest request){
        String refreshToken = request.getHeader("refreshToken");
        Long userId = userSupport.getCurrentUserID();
        userService.logOut(refreshToken,userId);
        return jsonResponse.success();
    }


    @PostMapping("/access-tokens")
    public jsonResponse<String> refreshAccessToken(HttpServletRequest request) throws Exception {
        String refreshToken = request.getHeader("refreshToken");
        String accessToken = userService.refreshAccessToken(refreshToken);
        return new jsonResponse<>(accessToken);
    }

    @GetMapping("/test")
    public String query(){
        return "test";
    }

    //分页查询用户列表
    @GetMapping("/user-infos")
    public jsonResponse<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer no,@RequestParam Integer size,String nick){
        Long userId = userSupport.getCurrentUserID();
        JSONObject params = new JSONObject();
        no = 1;
        size = 5;
        params.put("no",no);
        params.put("size",size);
        params.put("nick",nick);
        params.put("userId",userId);
        PageResult<UserInfo> res = userService.pageListUserInfos(params);
        if(res.getTotal()>0){
          List<UserInfo> cheachInfoList = userFollowingService.cheackFollowingStatus(res.getList(),userId);
          res.setList(cheachInfoList);
        }
        return new jsonResponse<>(res);
    }






}
