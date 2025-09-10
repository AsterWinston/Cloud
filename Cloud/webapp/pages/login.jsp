<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/login.css">
    <script src="${pageContext.request.contextPath}/resources/js/login.js" defer></script>
</head>
<body>
    <div class="login-container">
        <div class="login-box">
            <h2>Login</h2>
            <form action="login" method="POST" id="loginForm">
                <!-- 账户输入框 -->
                <div class="input-group">
                    <input type="text" name="name" id="name" placeholder="Username" value="${param.name}" required>
                </div>

                <!-- 密码输入框 -->
                <div class="input-group">
                    <input type="password" name="password" id="password" placeholder="Password" required>
                    <button type="button" id="showPassword" class="show-password">👁️</button>
                </div>

                <!-- 免密复选框 -->
                <div class="checkbox-group">
                    <input type="checkbox" id="rememberMe" name="rememberMe">
                    <label for="rememberMe">10 days remember me</label>
                </div>

                <!-- 提交按钮 -->
                <div class="submit-btn">
                    <button type="submit">Login</button>
                </div>

                <!-- 注册链接 -->
                <div class="register-link">
                    <a href="#">Register an account</a>
                </div>

            </form>
        </div>
    </div>
</body>
</html>
