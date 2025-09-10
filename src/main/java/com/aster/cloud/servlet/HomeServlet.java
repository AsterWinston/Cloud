package com.aster.cloud.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        System.out.println("经过HomeServlet的请求 = " + request.getRequestURI());

        request.getRequestDispatcher("pages/home/home.jsp").forward(request, response);
    }

}
