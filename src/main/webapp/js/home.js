// 上传文件功能（无修改）
document.getElementById('uploadBtn').addEventListener('click', function() {
    alert('上传文件功能，可在此处添加逻辑');
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
