<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.aster.cloud.beans.FileOrDirInformation" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="contextPath" content="${pageContext.request.contextPath}">
    <title>cloud</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/backend.css">
</head>
<body>
    <form id="getInDirForm" method="post" action="${pageContext.request.contextPath}/home">
        <input type="hidden" name="current_dir" id="destinationDirInput"/>
    </form>
    <form id="backDirForm" method="post" action="${pageContext.request.contextPath}/home">
        <input type="hidden" name="current_dir" id="parentDir"
        value="${parent_path != current_dir ? parent_path : ''}" />
    </form>
    <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/deleteFile">
        <input type="hidden" name="file_or_dir_to_delete" id="deleteFileInput"
        value="${current_dir}"/>
    </form>
    <form id="downloadForm" method="post" action="${pageContext.request.contextPath}/downloadFile">
        <input type="hidden" name="file_path_to_download" id="downloadFileInput"
        value="${current_dir}"/>
    </form>
    <form id="uploadForm" method="post"
          action="${pageContext.request.contextPath}/uploadFile"
          enctype="multipart/form-data">
        <input type="file" id="uploadInput" name="files"
               multiple style="display:none;" />
        <input type="hidden" name="path_to_upload" value="${current_dir}" />
    </form>
    <input type="hidden" name="current_dir" value="${current_dir}" id="currentDirInput">


    <div class="container">
        <!-- 左侧边栏 -->
        <div class="sidebar">
            <div class="user-info">
                <div class="user-container">
                    <span class="user-icon">👤</span>
                    <span class="user-name">${user_name}</span>
                </div>
            </div>
            <div class="sidebar-buttons-container">
                <a href="#" class="sidebar-link">用户管理</a>
                <a href="#" class="sidebar-link">访问控制</a>
                <a href="#" class="sidebar-link">登录日志</a>
                <a href="${pageContext.request.contextPath}/home" class="sidebar-link">返回首页</a>
                <a href="${pageContext.request.contextPath}/logout" class="sidebar-link">安全退出</a>

                <!-- 可以添加更多按钮 -->
            </div>
        </div>
        <!-- 主内容区 - 已移除顶部操作栏 -->
        <div class="main-content">
            <div class="content-wrapper">
                <div class="scrollable-content">
                    <!-- 内容区域 -->

                </div>
            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/home.js"></script>
    <script>
        var deleteSuccess = "${delete_success}";
        if (deleteSuccess) {
            alert("${delete_success}");
        }
        var deleteError = "${delete_error}";
        if (deleteError) {
            alert("${delete_error}");
        }
    </script>
</body>
</html>
