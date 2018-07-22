package com.zhenbianshu.util;

/**
 * created by zbs on 2018/7/22
 */
public interface Handler<T, R> {
    R handle(T input);
}
