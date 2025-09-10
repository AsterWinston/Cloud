// 上传文件功能（无修改）
document.getElementById('uploadBtn').addEventListener('click', function() {
    alert('上传文件功能，可在此处添加逻辑');
});

// 新建文件夹功能（无修改）
document.getElementById('createFolderBtn').addEventListener('click', function() {
    alert('新建文件夹功能，可在此处添加逻辑');
});

// 返回上级功能（无修改）
document.getElementById('backParentBtn').addEventListener('click', function() {
    alert('返回上级功能，可在此处添加逻辑（如跳转上级路径）');
});

// 新增：下载文件功能（示例）
function downloadFile(btn) {
    // 获取当前文件行的文件名（可根据实际需求调整）
    const fileName = btn.parentElement.querySelector('.file-name').textContent;
    alert(`开始下载文件：${fileName}（可在此处添加实际下载逻辑，如请求后端接口）`);
}

// 删除文件功能（无修改）
function deleteFile(btn) {
    const fileName = btn.parentElement.querySelector('.file-name').textContent;
    if (confirm(`确定要删除文件：${fileName}吗？`)) {
        alert(`删除文件：${fileName}（可在此处添加实际删除逻辑）`);
    }
}