package com.zhenbianshu.service;

import com.zhenbianshu.model.Whitelist;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

/**
 * created by zbs on 2018/6/23
 */
@Aspect
@Service
public class WhitelistAspect {

    @Before(value = "appkeyPointCut() && @annotation(whitelist)")
    public void checkAppkeyWhitelist(JoinPoint joinPoint, Whitelist whitelist) {
        System.out.println("aop");
    }


    @Pointcut("@annotation(com.zhenbianshu.model.Whitelist)")
    public void appkeyPointCut() {
    }
}
