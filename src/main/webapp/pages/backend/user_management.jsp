<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="contextPath" content="${pageContext.request.contextPath}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/backend.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user_management.css">

</head>
<body>
    <form id="navigateForm" method="post" action="${pageContext.request.contextPath}/backend">
        <input type="hidden" name="destination_page" id="destinationPage"/>
    </form>    <div class="container">
    <div id="createUserModal" class="hidden"></div>

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

                    <!-- 顶部功能区域 -->
                    <div class="top-function-area">
                        <div class="info-display">查到${user_count}条记录</div>
                        <div class="top-buttons">
                            <button class="top-btn" id="queryPasswordBtn">查询指定用户的密码</button>
                            <button class="top-btn" id="createUserBtn" onclick="showCreateUserModal()">创建用户</button>
                        </div>
                    </div>
                    <!-- 新增用户列表滚动容器 -->
                    <div class="user-list">
                        <div class="user-list">
                            <c:forEach var="user" items="${user_list}">
                                <div class="user-item">
                                    <div class="user-name-container">
                                        <span class="user-name-text">${user.userName}</span>
                                    </div>
                                    <div class="user-info-item">
                                        <span class="user-createtime">${user.createDate}</span>
                                    </div>
                                    <div class="user-info-item">
                                        <span class="user-limitvolume">${user.limitVolume}MB</span>
                                    </div>
                                    <div class="user-buttons">
                                        <button class="user-btn reset-limit-btn">重设限制容量</button>
                                        <button class="user-btn reset-pwd-btn">重设密码</button>
                                        <button class="user-btn delete-user-btn">删除用户</button>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        <!-- 其他用户项类似，此处省略 -->
                    </div>
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
    <script src="${pageContext.request.contextPath}/js/user_management.js"></script>
</body>
</html>