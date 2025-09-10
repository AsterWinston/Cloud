<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset_password.css">
</head>
<body>
    <div class="reset-container">
        <div class="reset-box">
            <h2>Reset Password</h2>

            <form action="${pageContext.request.contextPath}/resetPassword" method="POST" id="resetForm">
                <!-- 旧密码输入框 -->
                <div class="input-group">
                    <input type="password" name="old_password" id="old_password" placeholder="Old Password" required>
                </div>

                <!-- 新密码输入框 -->
                <div class="input-group">
                    <input type="password" name="new_password" id="new_password" placeholder="New Password" required>
                </div>

                <!-- 确认新密码输入框 -->
                <div class="input-group">
                    <input type="password" name="confirm_password" id="confirm_password" placeholder="Confirm New Password" required>
                </div>

                <!-- 按钮组：Reset 按钮 + Home 按钮（新增） -->
                <div class="btn-group">
                    <!-- Reset 提交按钮 -->
                    <button type="submit" class="reset-btn">Reset</button>
                    <!-- Home 跳转按钮（链接留白，自行填写） -->
                    <a href="${pageContext.request.contextPath}/home" class="home-btn">Home</a>
                </div>
            </form>
        </div>
    </div>

    <script>
        // 1. 读取后端传递的成功/失败信息
        const resetSuccess = "${reset_password_success}";
        const resetFail = "${reset_password_fail}";

        // 2. 弹窗提示
        if (resetFail && resetFail.trim() !== "") {
            alert(resetFail);
        } else if (resetSuccess && resetSuccess.trim() !== "") {
            alert(resetSuccess);
        }

        // 3. 前端验证两次密码一致性
        document.getElementById('resetForm').addEventListener('submit', function(e) {
            const newPassword = document.getElementById('new_password').value;
            const confirmPassword = document.getElementById('confirm_password').value;

            if (newPassword !== confirmPassword) {
                e.preventDefault();
                alert('New password and confirmation do not match!');
                return false;
            }
        });
    </script>
</body>
</html>