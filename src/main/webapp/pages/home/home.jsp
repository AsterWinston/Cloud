<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.aster.cloud.beans.FileOrDirInformation" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="contextPath" content="${pageContext.request.contextPath}">
    <title>cloud</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css">
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
        <!-- 左侧边栏（无修改） -->
        <div class="sidebar">
            <div class="user-info">
                <div class="user-container">
                    <span class="user-icon">👤</span>
                    <span class="user-name">${user_name}</span>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/resetPassword" class="sidebar-link">更改密码</a>
            <a href="#" class="sidebar-link">前往后台</a>
            <a href="${pageContext.request.contextPath}/logout" class="sidebar-link">安全退出</a>
        </div>
        <!-- 主内容区 -->
        <div class="main-content">
            <!-- 顶部操作栏（无修改） -->
            <div class="top-bar">
                <div class="path-container">
                    <span class="current-path">当前路径为：${relative_path}</span>
                </div>

                <a href="javascript:void(0);" class="action-btn" id="backParentBtn">返回上级</a>
                <div class="action-buttons">
                    <a href="javascript:void(0);" class="action-btn" id="uploadBtn">上传文件</a>
                    <a href="javascript:void(0);" class="action-btn" id="createFolderBtn">新建文件夹</a>
                </div>
            </div>
            <div id="progressContainer" style="display:none; margin:10px 0;">
                <div id="progressBar" style="height:8px; background:green; width:0%;"></div>
            </div>

            <!-- 文件列表区域（关键修改：下载按钮用download-btn类，删除按钮保留delete-btn类） -->
            <div class="file-list">
                <c:forEach var="file" items="${dir_info.dir_list}">
                    <div class="file-item">
                        <div class="file-name-container">
                            <c:choose>
                                <c:when test="${file.typeStr == 'DIRECTORY'}">
                                    <a href="javascript:void(0);"
                                       class="file-name"
                                       onclick="openDir('${dir_info.current_dir}/${file.name}')">${file.name}</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="javascript:void(0);"
                                       class="file-name"
                                       onclick="openDir('')">${file.name}</a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <span class="file-time">${file.time}</span>
                        <span class="file-size">${file.size}MB</span>
                        <a href="javascript:void(0);" class="download-btn" onclick="downloadFile(this)">下载</a>
                        <a href="javascript:void(0);" class="delete-btn" onclick="deleteFile(this)">删除</a>
                    </div>
                </c:forEach>

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