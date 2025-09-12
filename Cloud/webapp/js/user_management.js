document.addEventListener("DOMContentLoaded", function() {
    const contextPath = document.querySelector("meta[name='contextPath']").content;

    // 绑定所有删除按钮
    document.querySelectorAll(".delete-user-btn").forEach(btn => {
        btn.addEventListener("click", function() {
            const userName = this.closest(".user-item").querySelector(".user-name-text").textContent;

            if (confirm(`确定要删除用户 ${userName} 吗？`)) {
                const xhr = new XMLHttpRequest();
                xhr.open("POST", `${contextPath}/deleteUser`, true);
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

                xhr.onload = function() {
                    if (xhr.status === 200) {
                        const response = JSON.parse(xhr.responseText);
                        alert(response.message);
                        if (response.status === "success") {
                            window.location.reload(); // 删除成功就刷新页面
                        }
                    } else {
                        alert("请求失败，请稍后再试");
                    }
                };

                xhr.send(`user_name=${encodeURIComponent(userName)}`);
            }
        });
    });
});

document.addEventListener("DOMContentLoaded", function() {
    const contextPath = document.querySelector("meta[name='contextPath']").content;

    // 创建弹窗 HTML
    const modal = document.getElementById("createUserModal");
    modal.innerHTML = `
        <div class="modal-content">
            <input type="text" id="newUserName" placeholder="Enter username">
            <input type="password" id="newUserPassword" placeholder="Enter password">
            <input type="number" id="newUserLimitVolume" placeholder="Enter limit volume">
            <div class="modal-buttons">
                <button class="cancel-btn">Cancel</button>
                <button class="confirm-btn">Confirm</button>
            </div>
        </div>
    `;

    // 隐藏弹窗
    modal.style.display = "none";

    // 打开弹窗函数
    const openModal = () => { modal.style.display = "flex"; };
    // 关闭弹窗函数
    const closeModal = () => { modal.style.display = "none"; };

    // 给按钮绑定事件
    const cancelBtn = modal.querySelector(".cancel-btn");
    const confirmBtn = modal.querySelector(".confirm-btn");

    cancelBtn.addEventListener("click", closeModal);

    confirmBtn.addEventListener("click", function() {
        const userName = document.getElementById("newUserName").value.trim();
        const userPassword = document.getElementById("newUserPassword").value.trim();
        const limitVolume = document.getElementById("newUserLimitVolume").value.trim();

        if (!userName || !userPassword || !limitVolume) {
            alert("All fields are required");
            return;
        }

        // 创建 AJAX 请求
        const xhr = new XMLHttpRequest();
        xhr.open("POST", `${contextPath}/addUser`, true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

        xhr.onload = function() {
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                alert(response.message);
                if (response.status === "success") {
                    closeModal();
                    window.location.reload(); // 刷新用户列表
                }
            } else {
                alert("Request failed, please try again");
            }
        };

        // 发送请求
        const params = `user_name=${encodeURIComponent(userName)}&user_password=${encodeURIComponent(userPassword)}&limit_volume=${encodeURIComponent(limitVolume)}`;
        xhr.send(params);
    });

    // 暴露一个函数让按钮打开弹窗
    window.showCreateUserModal = openModal;
});
