package com.aster.cloud.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "AdminFilter")
public class AdminFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("经过AdminFilter的请求 = " + request.getRequestURI());
        if(request.getSession().getAttribute("user_name").equals(request.getSession().getServletContext().getAttribute("admin_name"))){
            System.out.println("管理员访问");
            chain.doFilter(request, response);
        } else {
            System.out.println("非管理员访问");
            response.sendRedirect(request.getContextPath() + "/home");
        }

    }
}
