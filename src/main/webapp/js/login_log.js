document.addEventListener('DOMContentLoaded', function() {

    // --- 下拉框每页条目数改变事件 ---
    const pageSizeSelect = document.getElementById('pageSize');
    const resetItemCountForm = document.getElementById('resetItemCountForm');
    const itemCountInput = document.getElementById('itemCountInputOfResetItemCountForm');

    if (pageSizeSelect && resetItemCountForm && itemCountInput) {
        pageSizeSelect.addEventListener('change', function() {
            itemCountInput.value = this.value;
            resetItemCountForm.submit();
        });
    }

    // --- 清空日志按钮事件 ---
    const clearLogBtn = document.getElementById('clearLogBtn');
    const clearLogForm = document.getElementById('clearLogForm');

    if (clearLogBtn && clearLogForm) {
        clearLogBtn.addEventListener('click', function(event) {
            event.preventDefault();
            if (confirm("确定要清空所有登录日志吗？此操作不可撤销！")) {
                clearLogForm.submit();
            }
        });
    }

    // --- 分页按钮事件 ---
    const prevPageBtn = document.getElementById('prevPageBtn');
    const nextPageBtn = document.getElementById('nextPageBtn');
    const switchPageForm = document.getElementById('switchPageForm');
    const switchPageCountInput = document.getElementById('pageCountInputOfSwitchPage');

    if (prevPageBtn && nextPageBtn && switchPageForm && switchPageCountInput) {
        prevPageBtn.addEventListener('click', function(event) {
            event.preventDefault();
            let currentPage = parseInt(switchPageCountInput.value, 10);
            if (currentPage > 1) {
                switchPageCountInput.value = currentPage - 1;
                switchPageForm.submit();
            } else {
                alert("已经是第一页了");
            }
        });

        nextPageBtn.addEventListener('click', function(event) {
            event.preventDefault();
            let currentPage = parseInt(switchPageCountInput.value, 10);
            const totalCount = parseInt(document.querySelector('meta[name="total_count"]').getAttribute('content'), 10);
            const itemCountEveryPage = parseInt(document.getElementById('itemCountInputOfSwitchPageForm').value, 10);
            const maxPage = Math.ceil(totalCount / itemCountEveryPage);
            if (currentPage < maxPage) {
                switchPageCountInput.value = currentPage + 1;
                switchPageForm.submit();
            } else {
                alert("已经是最后一页了");
            }
        });
    }

    // --- 单条日志删除按钮事件 ---
    const deleteLogForm = document.getElementById('deleteLogForm');
    const logIdInput = document.getElementById('logId');
    const deletePageCountInput = document.getElementById('pageCountInputOfDeleteLogForm'); // 删除表单的唯一 id
    const deleteItemCountInput = document.getElementById('itemCountInputOfDeleteLogForm');
    const logListContainer = document.querySelector('.log-list-scroll');
    const totalCountMeta = document.querySelector('meta[name="total_count"]');

    if (deleteLogForm && logIdInput && deletePageCountInput && deleteItemCountInput && logListContainer && totalCountMeta) {
        const deleteButtons = logListContainer.querySelectorAll('.delete-log-btn');

        deleteButtons.forEach(btn => {
            btn.setAttribute('type', 'button'); // 防止默认提交
            btn.addEventListener('click', function(event) {
                event.preventDefault();

                const logItem = btn.closest('.log-item');
                if (!logItem) return;

                const logId = logItem.querySelector('.log-info-item')?.textContent.trim();
                if (!logId) return;

                if (!confirm(`确定要删除日志 ID ${logId} 吗？`)) return;

                // 当前页码
                let currentPage = parseInt(deletePageCountInput.value, 10);

                // 总条数和每页条数
                const totalCount = parseInt(totalCountMeta.getAttribute('content'), 10);
                const itemCountEveryPage = parseInt(deleteItemCountInput.value, 10);

                // 删除后最大页数
                const newMaxPage = Math.ceil((totalCount - 1) / itemCountEveryPage);
                if (currentPage > newMaxPage && currentPage > 1) {
                    deletePageCountInput.value = currentPage - 1;
                }

                // 设置日志ID
                logIdInput.value = logId;

                // 提交删除表单
                deleteLogForm.submit();
            });
        });
    }

});
