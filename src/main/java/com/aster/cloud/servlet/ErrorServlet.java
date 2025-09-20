package com.aster.cloud.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/error")
public class ErrorServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        //用户访问（可以登陆用户）所有404页面将重定向到.../home
        System.out.println("经过ErrorServlet重定向的请求 = " + request.getRequestURI());
        response.sendRedirect(request.getContextPath() + "/home");//重定向必须加/在前
    }
}
