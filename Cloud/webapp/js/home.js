//全局变量
let isUploading = false; // 上传状态
const userDirectory = document.getElementById("destinationDirInput").value || ""; // 用于更新相对路径显示
const contextPathMeta = document.querySelector('meta[name="contextPath"]');
const contextPath = contextPathMeta ? contextPathMeta.getAttribute('content') : "";
let currentXHR = null;

//上传文件ajax
// 页面加载完成后初始化
document.addEventListener("DOMContentLoaded", function() {
    initUploadEvents();
});

/**
 * 初始化上传事件
 */
function initUploadEvents() {
    const uploadBtn = document.getElementById("uploadBtn");
    const uploadInput = document.getElementById("uploadInput");
    const progressContainer = document.getElementById("progressContainer");
    const progressBar = document.getElementById("progressBar");
    const pathInput = document.querySelector('#uploadForm input[name="path_to_upload"]');

    // 上传按钮点击事件
    uploadBtn.addEventListener("click", function() {
        if (isUploading) {
            alert("当前有文件正在上传，请等待完成后重试！");
            return;
        }
        uploadInput.click();
    });

    // 文件选择后处理（允许空文件）
    uploadInput.addEventListener("change", function() {
        const selectedFiles = this.files;
        if (selectedFiles.length === 0) return;

        // 显示进度条
        progressContainer.style.display = "block";
        progressBar.style.width = "0%";

        // 计算总文件大小(MB) - 保留4位小数，支持空文件(0MB)
        let totalFileSizeByte = 0;
        for (let i = 0; i < selectedFiles.length; i++) {
            totalFileSizeByte += selectedFiles[i].size;
        }
        const totalFileSizeMB = (totalFileSizeByte / (1024 * 1024)).toFixed(4);

        // 发送预检请求
        const precheckXHR = new XMLHttpRequest();
        precheckXHR.open("POST", `${contextPath}/precheckUpload`, true);
        precheckXHR.setRequestHeader("Content-Type", "application/json");

        // 预检请求成功处理
        precheckXHR.onload = function() {
            handlePrecheckResponse(
                precheckXHR,
                selectedFiles,
                pathInput.value,
                progressBar,
                progressContainer,
                uploadInput
            );
        };

        // 预检请求错误处理
        precheckXHR.onerror = function() {
            alert("网络异常：无法连接到服务器，请检查网络");
            resetUploadUI(progressContainer, uploadInput);
        };

        // 发送预检数据（包含空文件的0MB）
        precheckXHR.send(JSON.stringify({
            path_to_upload: pathInput.value,
            total_file_size_mb: totalFileSizeMB
        }));
    });
}

/**
 * 处理预检请求响应
 */
function handlePrecheckResponse(xhr, files, uploadPath, progressBar, progressContainer, uploadInput) {
    // 检查响应内容类型
    const contentType = xhr.getResponseHeader("Content-Type");
    if (!contentType || !contentType.includes("application/json")) {
        alert(`服务器响应格式错误：期望JSON，实际为${contentType || '未知类型'}`);
        resetUploadUI(progressContainer, uploadInput);
        return;
    }

    // 解析JSON响应
    let res;
    try {
        res = JSON.parse(xhr.responseText);
    } catch (e) {
        alert(`服务器返回无效JSON：${xhr.responseText.substring(0, 100)}...`);
        resetUploadUI(progressContainer, uploadInput);
        return;
    }

    // 处理不同状态码
    switch(xhr.status) {
        case 200:
            if (res.status === "allow") {
                startFileUpload(files, uploadPath, progressBar, progressContainer, uploadInput);
            } else {
                alert("上传失败：" + (res.message || "未知错误"));
                resetUploadUI(progressContainer, uploadInput);
            }
            break;

        case 413:
            alert("容量不足：" + (res.message || "文件大小超过存储限制"));
            resetUploadUI(progressContainer, uploadInput);
            break;

        case 403:
            alert("权限错误：" + (res.message || "无权限上传至该路径"));
            resetUploadUI(progressContainer, uploadInput);
            break;

        default:
            alert(`服务器错误：状态码${xhr.status}，${res.message || "请联系管理员"}`);
            resetUploadUI(progressContainer, uploadInput);
    }
}

/**
 * 执行文件上传
 */
function startFileUpload(files, uploadPath, progressBar, progressContainer, uploadInput) {
    isUploading = true;
    const formData = new FormData();

    // 添加所有文件（包括空文件）
    for (let i = 0; i < files.length; i++) {
        formData.append("files", files[i]);
    }
    formData.append("path_to_upload", uploadPath);

    currentXHR = new XMLHttpRequest();
    currentXHR.open("POST", `${contextPath}/uploadFile`, true);

    // 进度监听
    currentXHR.upload.addEventListener("progress", function(e) {
        if (!isUploading || !e.lengthComputable) return;
        const percent = Math.round((e.loaded / e.total) * 100);
        progressBar.style.width = percent + "%";
    });

    // 上传响应处理
    currentXHR.onreadystatechange = function() {
        if (currentXHR.readyState === 4) {
            handleUploadResponse(currentXHR, progressContainer, uploadInput);
        }
    };

    // 上传错误处理
    currentXHR.onerror = function() {
        alert("网络异常：上传过程中连接中断");
        resetUploadUI(progressContainer, uploadInput);
    };

    currentXHR.send(formData);
}

/**
 * 处理上传响应
 */
function handleUploadResponse(xhr, progressContainer, uploadInput) {
    resetUploadUI(progressContainer, uploadInput);

    // 处理TCP连接被关闭的情况
    if (xhr.status === 0) {
        alert("容量不足：文件大小超过存储限制");
        return;
    }

    // 检查响应内容类型
    const contentType = xhr.getResponseHeader("Content-Type");
    if (!contentType || !contentType.includes("application/json")) {
        alert(`服务器响应格式错误：期望JSON，实际为${contentType || '未知类型'}`);
        return;
    }

    // 解析响应
    let res;
    try {
        res = JSON.parse(xhr.responseText);
    } catch (e) {
        alert(`服务器返回无效JSON：${xhr.responseText.substring(0, 100)}...`);
        return;
    }

    // 处理响应
    if (xhr.status === 200) {
        if (res.status === "success") {
            alert("上传成功！");
            window.location.reload();
        } else {
            alert("上传失败：" + res.message);
        }
    } else if (xhr.status === 413) {
        alert("容量不足：" + (res.message || "文件大小超过存储限制"));
    } else {
        alert(`上传失败：服务器错误（状态码${xhr.status}）`);
    }
}

/**
 * 重置上传UI状态
 */
function resetUploadUI(progressContainer, uploadInput) {
    isUploading = false;
    currentXHR = null;
    progressContainer.style.display = "none";
    document.getElementById("progressBar").style.width = "0%";
    uploadInput.value = "";
}

//其他功能
//检查上传状态
function checkUploadStatus() {
    if(isUploading) {
        alert("当前有文件正在上传，请等待上传完成！");
        return false;
    }
    return true;
}

//下载文件，新发请求
function downloadFile(btn) {
    if(!checkUploadStatus()) return;
    const fileName = btn.parentElement.querySelector('.file-name').textContent;
    const currentDir = document.getElementById("downloadFileInput").value;
    document.getElementById("downloadFileInput").value = currentDir + "/" + fileName;
    document.getElementById("downloadForm").submit();
    document.getElementById("downloadFileInput").value = currentDir;
}

//删除文件，ajax
function deleteFile(btn) {
    if (!checkUploadStatus()) return; // 上传锁检查

    const fileName = btn.parentElement.querySelector('.file-name').textContent; // 获取文件名
    if (confirm(`确定要删除文件（夹）${fileName}吗？`)) {
        // 获取当前目录
        const currentDir = document.getElementById("deleteFileInput").value;

        // 拼接文件路径
        const filePath = currentDir + "/" + fileName;

        // 创建 AJAX 请求
        const xhr = new XMLHttpRequest();
        xhr.open("POST", `${contextPath}/deleteFile`, true); // 设置删除文件的目标 URL

        // 设置请求头
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

        // 处理响应
        xhr.onload = function() {
            const response = JSON.parse(xhr.responseText); // 解析返回的 JSON 数据
            if (xhr.status === 200) {
                if (response.status === "success") {
                    alert(response.message); // 删除成功提示
                    window.location.reload(); // 刷新页面或更新文件列表
                } else {
                    alert(response.message); // 删除失败或拒绝删除提示
                }
            } else {
                alert("请求失败，无法处理删除操作");
            }
        };

        // 发送请求（传递文件路径）
        xhr.send(`file_or_dir_to_delete=${encodeURIComponent(filePath)}`);
    }
}



//进入目录，新发请求
function openDir(path) {
    if(!checkUploadStatus()) return;
    if (!path) return; // 文件点击不跳转
    document.getElementById("destinationDirInput").value = path;
    document.getElementById("getInDirForm").submit();
}

//返回上级，新发请求
document.addEventListener("DOMContentLoaded", function() {
    const backBtn = document.getElementById("backParentBtn");
    const dirForm = document.getElementById("backDirForm");
    const currentDirInput = document.getElementById("parentDir");

    backBtn.addEventListener("click", function() {
        if(!checkUploadStatus()) return;
        if (currentDirInput.value && currentDirInput.value.trim() !== "") {
            dirForm.submit();
        } else {
            alert("已经是根目录，无需返回上级！");
        }
    });
});

//创建目录，ajax
document.getElementById('createFolderBtn').addEventListener('click', function() {
    if (!checkUploadStatus()) return; // 上传锁检查

    let folderName = prompt("请输入新文件夹名称：");
    if (!folderName || folderName.trim() === "") return;

    folderName = folderName.trim();

    // 获取当前路径
    const currentDir = document.getElementById("currentDirInput").value;

    // 拼接新文件夹完整路径
    const newFolderPath = currentDir + "/" + folderName;

    // AJAX 请求
    const xhr = new XMLHttpRequest();
    xhr.open("POST", `${contextPath}/createFolder`, true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onload = function() {
        if (xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            if (response.status === "success") {
                alert(response.message);
                window.location.reload(); // 刷新页面显示新文件夹
            } else {
                alert(response.message);
            }
        } else {
            const response = JSON.parse(xhr.responseText);
            alert(response.message || "文件夹创建失败");
        }
    }

    // 发送当前目录和新文件夹名
    xhr.send(`folderPath=${encodeURIComponent(newFolderPath)}`);
});




//阻止上传期间修改密码或后台跳转
document.querySelectorAll(".sidebar-link").forEach(link => {
    link.addEventListener("click", function(e) {
        if(isUploading) {
            e.preventDefault();
            alert("当前有文件正在上传，请等待上传完成！");
        }
    });
});
