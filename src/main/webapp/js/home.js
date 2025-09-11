document.getElementById("uploadBtn").addEventListener("click", function() {
    document.getElementById("uploadInput").click();
});

document.getElementById("uploadInput").addEventListener("change", function() {
    const fileInput = this;
    const form = document.getElementById("uploadForm");
    const progressContainer = document.getElementById("progressContainer");
    const progressBar = document.getElementById("progressBar");

    if(fileInput.files.length === 0) return; // 没选择文件就返回

    // 显示进度条
    progressContainer.style.display = "block";
    progressBar.style.width = "0%";

    const formData = new FormData(form);
    const xhr = new XMLHttpRequest();

    xhr.open("POST", form.action, true);

    // 上传进度
    xhr.upload.addEventListener("progress", function(e) {
        if(e.lengthComputable) {
            const percent = (e.loaded / e.total) * 100;
            progressBar.style.width = percent + "%";
        }
    });

    // 上传完成
    xhr.onload = function() {
        if(xhr.status === 200) {
            alert("上传成功");
        } else {
            alert("上传失败");
        }
        // 隐藏进度条
        progressContainer.style.display = "none";
        progressBar.style.width = "0%";

        // 可选：刷新页面显示新文件
        window.location.reload();
    }

    xhr.send(formData);
});



// 新建文件夹功能（无修改）
document.getElementById('createFolderBtn').addEventListener('click', function() {
    alert('新建文件夹功能，可在此处添加逻辑');
});



// 新增：下载文件功能（示例）
function downloadFile(btn) {
    // 获取当前文件行的文件名（可根据实际需求调整）
    const fileName = btn.parentElement.querySelector('.file-name').textContent;
    const currentDir = document.getElementById("downloadFileInput").value;
    document.getElementById("downloadFileInput").value = currentDir + "/" + fileName;
    document.getElementById("downloadForm").submit();
    document.getElementById("downloadFileInput").value = currentDir;
}

// 删除文件功能（无修改）
function deleteFile(btn) {
    const fileName = btn.parentElement.querySelector('.file-name').textContent;
    if (confirm(`确定要删除文件${fileName}吗？`)) {
        // 拼接完整路径
        const currentDir = document.getElementById("deleteFileInput").value;
        document.getElementById("deleteFileInput").value = currentDir + "/" + fileName;
        // 提交隐藏表单
        document.getElementById("deleteForm").submit();
    }
}


function openDir(path) {
    if (!path) {
        // path为空 → 说明是文件 → 不提交表单
        console.log("点击了文件，不跳转。");
        return;
    }
    document.getElementById("destinationDirInput").value = path;
    document.getElementById("getInDirForm").submit();
}

document.addEventListener("DOMContentLoaded", function() {
    var backBtn = document.getElementById("backParentBtn");
    var dirForm = document.getElementById("backDirForm");
    var currentDirInput = document.getElementById("parentDir");

    backBtn.addEventListener("click", function() {
        // 判断隐藏表单的值是否为空
        if (currentDirInput.value && currentDirInput.value.trim() !== "") {
            dirForm.submit();  // 不为空就提交表单
        } else {
            // 值为空，不发送请求
            alert("已经是根目录，无需返回上级！");
        }
    });
});
