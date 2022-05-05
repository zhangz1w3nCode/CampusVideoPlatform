package com.zzw.bilibili.api;

import com.zzw.Entity.User;
import com.zzw.Entity.followingGroup;
import com.zzw.Entity.userFollowing;
import com.zzw.bilibili.api.support.userSupport;
import com.zzw.domain.jsonResponse;
import com.zzw.service.userFollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class userFollow {
    @Autowired
    private userSupport userSupport;

    @Autowired
    private userFollowingService userFollowingService;

    //添加关注
    @PostMapping("/user-followings")
    public jsonResponse<String> addUserFollowing(@RequestBody userFollowing userFollowing){
        Long userID = userSupport.getCurrentUserID();
        userFollowing.setUserId(userID);
        userFollowingService.addUserFollowings(userFollowing);
        return  jsonResponse.success();
    }

    //获取用户的关注列表
    @GetMapping("/user-followings")
    public jsonResponse<List<followingGroup>> getUserFollowings(){
        Long userID = userSupport.getCurrentUserID();
        List<followingGroup> userFolloings = userFollowingService.getUserFolloings(userID);
        return new jsonResponse<>(userFolloings);
    }

    //获取用户的粉丝列表
    @GetMapping("/user-fans")
    public jsonResponse<List<userFollowing>> getUserFans(){
        Long userID = userSupport.getCurrentUserID();
        List<userFollowing> res = userFollowingService.getUserFans(userID);
        return new jsonResponse<>(res);
    }

    //添加用户关注分组
    @PostMapping("/user-following-groups")
    public jsonResponse<Long> addUserFollowingGroups(@RequestBody followingGroup followingGroup){
        Long userID = userSupport.getCurrentUserID();
        followingGroup.setUserId(userID);
        Long groupId = userFollowingService.addUserFollowingGroups(followingGroup);
        return new jsonResponse<>(groupId);
    }

    //获取用户关注分组
    @GetMapping("/user-following-groups")
    public jsonResponse<List<followingGroup>> addUserFollowingGroups(){
        Long userID = userSupport.getCurrentUserID();
        List<followingGroup> res = userFollowingService.getUserFollowingGroups(userID);
        return new jsonResponse<>(res);
    }




}
