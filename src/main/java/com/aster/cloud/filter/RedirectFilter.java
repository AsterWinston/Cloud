package com.aster.cloud.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebFilter(filterName = "RedirectFilter")
public class RedirectFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //所有的.jsp不能直接访问，如果访问则重定向到主页.../home
        System.out.println("经过RedirectFilter重定向的请求 = " + request.getRequestURI());

        response.sendRedirect(request.getContextPath() + "/home");//重定向需要/
    }
}
