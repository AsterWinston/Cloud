<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css">
</head>
<body>
    <div class="container">
        <!-- 左侧边栏（无修改） -->
        <div class="sidebar">
            <div class="user-info">
                <div class="user-container">
                    <span class="user-icon">👤</span>
                    <span class="user-name">${username}</span>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/resetPassword" class="sidebar-link">修改密码</a>
            <a href="#" class="sidebar-link">前往后台</a>
            <a href="${pageContext.request.contextPath}/logout" class="sidebar-link">安全退出</a>
        </div>
        <!-- 主内容区 -->
        <div class="main-content">
            <!-- 顶部操作栏（无修改） -->
            <div class="top-bar">
                <div class="path-container">
                    <span class="current-path">当前路径为：/home/user/documents/projects/web-development/file-management-system/assets/images/icons/svg</span>
                </div>
                <a href="javascript:void(0);" class="action-btn" id="backParentBtn">返回上级</a>
                <div class="action-buttons">
                    <a href="javascript:void(0);" class="action-btn" id="uploadBtn">上传文件</a>
                    <a href="javascript:void(0);" class="action-btn" id="createFolderBtn">新建文件夹</a>
                </div>
            </div>

            <!-- 文件列表区域（关键修改：下载按钮用download-btn类，删除按钮保留delete-btn类） -->
            <div class="file-list">
                <div class="file-item">
                    <div class="file-name-container">
                        <a href="#" class="file-name">这是一个非常长的文件名示例，用于测试水平滚动效果.txt</a>
                    </div>
                    <span class="file-time">2023-10-01 12:30</span>
                    <span class="file-size">2.5MB</span>
                    <a href="javascript:void(0);" class="download-btn" onclick="downloadFile(this)">下载</a>
                    <a href="javascript:void(0);" class="delete-btn" onclick="deleteFile(this)">删除</a>
                </div>
                <div class="file-item">
                    <div class="file-name-container">
                        <a href="#" class="file-name">文件（夹）2</a>
                    </div>
                    <span class="file-time">2023-10-02 15:45</span>
                    <span class="file-size">1.8MB</span>
                    <a href="javascript:void(0);" class="download-btn" onclick="downloadFile(this)">下载</a>
                    <a href="javascript:void(0);" class="delete-btn" onclick="deleteFile(this)">删除</a>
                </div>
            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/home.js"></script>
</body>
</html>