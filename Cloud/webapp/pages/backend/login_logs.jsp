<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.aster.cloud.beans.FileOrDirInformation" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="contextPath" content="${pageContext.request.contextPath}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/backend.css">
</head>
<body>
    <form id="navigateForm" method="post" action="${pageContext.request.contextPath}/backend">
        <input type="hidden" name="destination_page" id="destinationPage"/>
    </form>
    <div class="container">
        <!-- 左侧边栏 - 与home页面结构完全一致 -->
        <div class="sidebar">
            <div class="user-info">
                <div class="user-container">
                    <span class="user-icon">👤</span>
                    <span class="user-name">${user_name}</span>
                </div>
            </div>
            <!-- 使用与home页面相同的滚动容器类名 -->
            <div class="sidebar-scroll-container">
                <a href="#" class="sidebar-link" onclick="goToPage('user_management')">用户管理</a>
                <a href="#" class="sidebar-link" onclick="goToPage('access_control')">访问控制</a>
                <a href="#" class="sidebar-link" onclick="goToPage('login_logs')">登录日志</a>
                <a href="${pageContext.request.contextPath}/home" class="sidebar-link">返回首页</a>
                <a href="${pageContext.request.contextPath}/logout" class="sidebar-link">安全退出</a>
            </div>
        </div>
        <!-- 主内容区 -->
        <div class="main-content">
            <div class="content-wrapper">
                <div class="scrollable-content">
                    <!-- 内容区域 -->
                    <h1>登陆日志</h1>
                </div>
            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/home.js"></script>
    <script>
        function goToPage(page) {
            // 动态设置隐藏表单的目标页面
            document.getElementById("destinationPage").value = page;
            // 提交表单
            document.getElementById("navigateForm").submit();
        }
    </script>
</body>
</html>
