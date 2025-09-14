document.addEventListener("DOMContentLoaded", () => {
    const saveBtn = document.getElementById("saveModificationBtn");
    const form = document.getElementById("blacklistForm");

    saveBtn.addEventListener("click", () => {
        // 手动收集表单数据
        const params = new URLSearchParams();
        params.append("destination_page", form.destination_page.value);
        params.append("ip_black_list", document.getElementById("ipBlacklist").value);
        params.append("ua_black_list", document.getElementById("uaBlacklist").value);

        fetch(form.action, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
            body: params.toString()
        })
        .then(res => res.json())
        .then(data => {
            if (data.status === "success") {
                alert(data.message);
                window.location.reload();
            } else {
                alert("保存失败：" + data.message);
            }
        })
        .catch(err => {
            console.error("请求出错：", err);
            alert("服务器错误");
        });
    });

    // 清空按钮
    document.getElementById("clearBlacklistBtn").addEventListener("click", () => {
        document.getElementById("ipBlacklist").value = "";
        document.getElementById("uaBlacklist").value = "";
    });
});
