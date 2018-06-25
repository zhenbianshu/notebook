package com.zhenbianshu.service;

import com.zhenbianshu.model.AuthParam;
import com.zhenbianshu.model.Whitelist;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * created by zbs on 2018/6/23
 */
@Component
public class AuthParamResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AuthParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Whitelist whitelist = parameter.getMethodAnnotation(Whitelist.class);
        // 通过 webRequest 和 whitelist 校验白名单
        System.out.println("interceptor");
        return new AuthParam();
    }
}
