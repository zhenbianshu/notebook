package com.zhenbianshu.model;

import java.lang.annotation.*;

/**
 * created by zbs on 2018/6/23
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Whitelist {
    String values() default "";
}
