<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="contextPath" content="${pageContext.request.contextPath}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/backend.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login_log.css">
    <meta name="total_count" content="${total_count}">
</head>
<body>
    <form id="resetItemCountForm" method="post" action="${pageContext.request.contextPath}/backend">
        <input type="hidden" name="destination_page" value="login_log">
        <!-- 指定功能为 reset_item_count_every_page -->
        <input type="hidden" name="function" value="reset_item_count_every_page">
        <!-- 每页条目数，由用户选择 -->
        <input type="hidden" name="item_count_every_page" id="itemCountInputOfResetItemCountForm">
    </form>
    <form id="clearLogForm" method="post" action="${pageContext.request.contextPath}/backend">
            <input type="hidden" name="destination_page" value="login_log">
            <!-- 指定功能为 reset_item_count_every_page -->
            <input type="hidden" name="function" value="clear_log">
            <!-- 每页条目数，由用户选择 -->
            <input type="hidden" name="item_count_every_page" id="itemCountInputOfClearLogForm" value="${item_count_every_page}">
    </form>
    <form id="switchPageForm" method="post" action="${pageContext.request.contextPath}/backend">
                <input type="hidden" name="destination_page" value="login_log">
                <!-- 指定功能为 reset_item_count_every_page -->
                <input type="hidden" name="function" value="switch_page">
                <!-- 每页条目数，由用户选择 -->
                <input type="hidden" name="item_count_every_page" id="itemCountInputOfSwitchPageForm" value="${item_count_every_page}">
                <input type="hidden" name="page_count" id="pageCountInputOfSwitchPage" value="${page_count}">
    </form>
    <form id="deleteLogForm" method="post" action="${pageContext.request.contextPath}/backend">
        <input type="hidden" name="destination_page" value="login_log">
        <input type="hidden" name="function" value="delete_log">
        <input type="hidden" name="item_count_every_page" id="itemCountInputOfDeleteLogForm" value="${item_count_every_page}">
        <input type="hidden" name="page_count" id="pageCountInputOfDeleteLogForm" value="${page_count}">
        <input type="hidden" name="log_id" id="logId">
    </form>
    <form id="navigateForm" method="post" action="${pageContext.request.contextPath}/backend">
        <input type="hidden" name="destination_page" id="destinationPage"/>
    </form>
    <div class="container">
        <!-- 左侧边栏 -->
        <div class="sidebar">
            <div class="user-info">
                <div class="user-container">
                    <span class="user-icon">👤</span>
                    <span class="user-name">${user_name}</span>
                </div>
            </div>
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
                        <div class="info-display">查到${total_count}条记录</div>
                        <div class="top-buttons">
                            <button class="top-btn" id="clearLogBtn">清空记录</button>
                        </div>
                    </div>

                    <!-- 日志列表滚动容器 -->
                    <div class="log-list-scroll">
                        <c:forEach var="log" items="${login_log_list}">
                            <div class="log-item">
                                <div class="log-info-item">${log.id}</div>
                                <div class="log-info-item">${log.name}</div>
                                <div class="log-info-item">${log.loginTime}</div>
                                <div class="log-info-item">${log.loginIP}</div>
                                <div class="log-buttons">
                                    <button type="button" class="log-btn delete-log-btn">delete</button>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- 底部分页区域 -->
                    <div class="bottom-function-area">
                        <div class="page-size-selector">
                            <label for="pageSize">每页显示</label>
                            <select id="pageSize" name="pageSize">
                                <option value="10" ${item_count_every_page == '10' ? 'selected' : ''}>10</option>
                                <option value="50" ${item_count_every_page == '50' ? 'selected' : ''}>50</option>
                                <option value="100" ${item_count_every_page == '100' ? 'selected' : ''}>100</option>
                                <option value="200" ${item_count_every_page == '200' ? 'selected' : ''}>200</option>
                            </select>
                            <label for="pageSize">条</label>
                        </div>
                        <div class="pagination-buttons">
                            <button class="pagination-btn" id="prevPageBtn">上一页</button>
                            <span id="currentPage">${page_count}</span>
                            <button class="pagination-btn" id="nextPageBtn">下一页</button>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
    <script>
        function goToPage(page) {
            document.getElementById("destinationPage").value = page;
            document.getElementById("navigateForm").submit();
        }
    </script>
    <script src="${pageContext.request.contextPath}/js/login_log.js"></script>
</body>
</html>
