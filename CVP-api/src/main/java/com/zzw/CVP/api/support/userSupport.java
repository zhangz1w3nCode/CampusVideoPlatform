package com.zzw.bilibili.api.support;

import com.zzw.exception.conditionException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.zzw.service.utils.tokenUtil;

@Component
public class userSupport {
    public Long getCurrentUserID(){
        ServletRequestAttributes requestAttributes =(ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
            //请求头 拿 token
        String token = requestAttributes.getRequest().getHeader("token");
        System.out.println(token);

        Long userID = tokenUtil.verifyToken(token);//验证 token 合法性
        System.out.println(userID);
        if(userID<0){
            throw  new conditionException("非法用户！");
        }

        return userID;

    }
}
