package com.alison.redpackets.utils;

import java.lang.reflect.Method;

/**
 * 定义拦截器接口
 */
public interface Interceptor {
    public boolean before(Object proxy, Object target, Method method, Object[] args);
    public void around(Object proxy, Object target, Method method, Object[] args);
    public void after(Object proxy, Object target, Method method, Object[] args);
}
