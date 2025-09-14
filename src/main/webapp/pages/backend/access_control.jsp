<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.aster.cloud.beans.FileOrDirInformation" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="contextPath" content="${pageContext.request.contextPath}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/backend.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/access_control.css">
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
                <a href="#" class="sidebar-link" onclick="goToPage('login_log')">登录日志</a>
                <a href="${pageContext.request.contextPath}/home" class="sidebar-link">返回首页</a>
                <a href="${pageContext.request.contextPath}/logout" class="sidebar-link">安全退出</a>
            </div>
        </div>
        <!-- 主内容区 -->
        <div class="main-content">
            <div class="content-wrapper">
                <div class="scrollable-content">
                    <form id="blacklistForm" method="post" action="${pageContext.request.contextPath}/saveBlackList">
                        <input type="hidden" name="destination_page" value="access_control">
                        <div class="blacklist-section">
                            <h2>IP黑名单</h2>
                            <textarea name="ip_black_list" id="ipBlacklist" rows="10" cols="50" placeholder="每行填写一个IP">${ip_black_list}</textarea>
                            <p>每行填写一个IP，例如192.168.1.1</p>
                        </div>

                        <div class="blacklist-section">
                            <h2>UA黑名单</h2>
                            <textarea name="ua_black_list" id="uaBlacklist" rows="10" cols="50" placeholder="每行填写一个UA"></textarea>
                            <p>每行填写一个User-Agent，例如Mozilla/5.0（暂不支持）</p>
                        </div>

                        <div class="form-buttons">
                            <button type="button" class="top-btn" id="clearBlacklistBtn">清空所有</button>
                            <button type="button" class="top-btn" id="saveModificationBtn">保存修改</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <script>
        function goToPage(page) {
            // 动态设置隐藏表单的目标页面
            document.getElementById("destinationPage").value = page;
            // 提交表单
            document.getElementById("navigateForm").submit();
        }
    </script>
    <script src="${pageContext.request.contextPath}/js/access_control.js"></script>
</body>
</html>
