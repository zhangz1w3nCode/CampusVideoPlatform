package com.zzw.bilibili.api.Aspect;

import com.zzw.Entity.auth.userRole;
import com.zzw.Entity.userMoment;
import com.zzw.annotation.ApiLimitedRole;
import com.zzw.bilibili.api.support.userSupport;
import com.zzw.constant.authRoleConstant;
import com.zzw.exception.conditionException;
import com.zzw.service.userRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//切面
@Order(1)
@Component
@Aspect
public class DataLimitedAspect {

    @Autowired
    private userSupport userSupport;

    @Autowired
    private userRoleService userRoleService;

    @Pointcut("@annotation(com.zzw.annotation.DataLimited)")
    public void check(){
    }

    @Before("check()") //传入限制的角色
    public void doBefore(JoinPoint joinPoint){
        Long userID = userSupport.getCurrentUserID();
        System.out.println(userID);
        List<userRole> userRoleList = userRoleService.getUserRoleByUserId(userID);

        //通过当前id 查询到的roleCode
        Set<String> roleCodeSet = userRoleList.stream().map(userRole::getCode).collect(Collectors.toSet());
        for (String str : roleCodeSet ) {
            System.out.println(str);
        }

        Object[] args = joinPoint.getArgs();

        for(Object obj :args){

            if(obj instanceof userMoment){
                userMoment userMoment = (userMoment)obj;
                String type = userMoment.getType();
                if(roleCodeSet.contains(authRoleConstant.ROLE_CODE_LV0) && !"0".equals(type)){
                    throw new conditionException("参数异常");
                }
            }

        }

    }




}
