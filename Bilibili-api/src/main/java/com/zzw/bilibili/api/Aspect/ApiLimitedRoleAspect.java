package com.zzw.bilibili.api.Aspect;

import com.zzw.Entity.auth.userRole;
import com.zzw.annotation.ApiLimitedRole;
import com.zzw.bilibili.api.support.userSupport;
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
public class ApiLimitedRoleAspect {

    @Autowired
    private userSupport userSupport;

    @Autowired
    private userRoleService userRoleService;

    @Pointcut("@annotation(com.zzw.annotation.ApiLimitedRole)")
    public void check(){
    }

    @Before("check() && @annotation(apiLimitedRole)") //传入限制的角色
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole){
        Long userID = userSupport.getCurrentUserID();
        System.out.println(userID);
        List<userRole> userRoleList = userRoleService.getUserRoleByUserId(userID);

        //要限制的角色
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();

        //限制角色 set
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        //通过当前id 查询到的roleCode
        Set<String> roleCodeSet = userRoleList.stream().map(userRole::getCode).collect(Collectors.toSet());
        for (String str : roleCodeSet ) {
            System.out.println(str);
        }
        //去两者交集 到 roleCodeSet
        roleCodeSet.retainAll(limitedRoleCodeSet);

        //roleCodeSet 有值说明 两者有交集 则 当前的用户是被限制的用户
        if(roleCodeSet.size()>0){
            throw new conditionException("权限不足！");
        }
    }




}
