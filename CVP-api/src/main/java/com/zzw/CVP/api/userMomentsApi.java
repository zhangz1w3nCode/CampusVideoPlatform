package com.zzw.bilibili.api;


import com.zzw.Entity.followingGroup;
import com.zzw.Entity.userMoment;
import com.zzw.annotation.ApiLimitedRole;
import com.zzw.annotation.DataLimited;
import com.zzw.bilibili.api.support.userSupport;
import com.zzw.domain.jsonResponse;
import com.zzw.service.userFollowingService;
import com.zzw.service.userMomentService;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.zzw.constant.authRoleConstant;
import java.util.List;

@RestController
@CrossOrigin
//用户动态相关 功能
public class userMomentsApi {

    @Autowired
    private userMomentService userMomentService;

    @Autowired
    private userSupport userSupport;


    //用户发动态-保存数据
    //使用spring aop 去做接口 调用权限的调用
    @ApiLimitedRole(limitedRoleCodeList = {authRoleConstant.ROLE_CODE_LV0})
    @DataLimited
    @PostMapping("/user-moments")
    public jsonResponse<String> addUserMoments(@RequestBody userMoment userMoment) throws Exception {
        Long userID = userSupport.getCurrentUserID();
        userMoment.setUserId(userID);
        userMomentService.addUserMoments(userMoment);
        return jsonResponse.success();
    }

    //查看我关注的人 发的动态-rocketMQ推送消息
    @PostMapping("/user-subscribed-moments")
    public jsonResponse<List<userMoment>> getUserSubscribedMoments() throws Exception {
        Long userID = userSupport.getCurrentUserID();
        List<userMoment> res = userMomentService.getUserSubscribedMoments(userID);
        return new jsonResponse<>(res);
    }


}
