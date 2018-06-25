package com.zhenbianshu.service;

import javax.servlet.*;
import java.io.IOException;

/**
 * created by zbs on 2018/6/23
 */
public class WhitelistFilter implements javax.servlet.Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("filter");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
