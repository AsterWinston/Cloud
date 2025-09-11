//全局变量
let isUploading = false; // 上传状态
const userDirectory = document.getElementById("destinationDirInput").value || ""; // 用于更新相对路径显示
const contextPathMeta = document.querySelector('meta[name="contextPath"]');
const contextPath = contextPathMeta ? contextPathMeta.getAttribute('content') : "";
//上传文件，ajax
document.getElementById("uploadBtn").addEventListener("click", function() {
    if(isUploading){
        alert("当前有文件正在上传，请等待上传完成！");
        return;
    }
    document.getElementById("uploadInput").click();
});

document.getElementById("uploadInput").addEventListener("change", function() {
    const fileInput = this;
    const form = document.getElementById("uploadForm");
    const progressContainer = document.getElementById("progressContainer");
    const progressBar = document.getElementById("progressBar");

    if(fileInput.files.length === 0) return;

    isUploading = true; // 开始上传
    progressContainer.style.display = "block";
    progressBar.style.width = "0%";

    const formData = new FormData(form);
    const xhr = new XMLHttpRequest();

    xhr.open("POST", form.action, true);

    xhr.upload.addEventListener("progress", function(e) {
        if(e.lengthComputable) {
            const percent = (e.loaded / e.total) * 100;
            progressBar.style.width = percent + "%";
        }
    });

    xhr.onload = function() {
        isUploading = false; // 上传结束
        progressContainer.style.display = "none";
        progressBar.style.width = "0%";

        if(xhr.status === 200) {
            alert("上传成功");
        } else {
            alert("上传失败");
        }

        // 刷新页面显示新文件
        window.location.reload();
    }

    xhr.send(formData);
});

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
