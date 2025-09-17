package com.aster.cloud.servlet;
import com.aster.cloud.utils.FileManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/precheckUpload")
public class PrecheckUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("经过PrecheckUploadServlet的请求 = " + request.getRequestURI());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 读取请求参数
            BufferedReader br = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            // 处理空请求体
            if (requestBody.isEmpty()) {
                sendErrorResponse(out, response, HttpServletResponse.SC_BAD_REQUEST, "请选择要上传的文件");
                return;
            }

            JSONObject reqJson = new JSONObject(requestBody);
            String pathToUpload = reqJson.optString("path_to_upload");
            double totalFileSizeMB = reqJson.optDouble("total_file_size_mb", -1);

            // 验证路径参数
            if (pathToUpload == null || pathToUpload.trim().isEmpty()) {
                sendErrorResponse(out, response, HttpServletResponse.SC_BAD_REQUEST, "上传路径不能为空");
                return;
            }

            // 允许文件大小为0（移除大小为0的校验）
            if (totalFileSizeMB < 0) {
                sendErrorResponse(out, response, HttpServletResponse.SC_BAD_REQUEST, "无法获取文件大小，请重试");
                return;
            }

            // 路径权限验证
            String userDirectory = (String) request.getSession().getAttribute("user_directory");
            if (userDirectory == null || !pathToUpload.startsWith(userDirectory.replace("\\", "/"))) {
                sendErrorResponse(out, response, HttpServletResponse.SC_FORBIDDEN, "无权限上传至该路径");
                return;
            }

            // 容量限制验证（空文件0MB不影响容量）
            long sizeNowMB = FileManager.getSizeNow(request);
            long limitSizeMB = FileManager.getLimitSize(request);

            // 增加容差，避免计算误差
            double tolerance = 1.0 / 1024; // 1KB容差
            if (sizeNowMB + totalFileSizeMB > limitSizeMB + tolerance) {
                String message = String.format(
                        "文件大小超过存储限制（已用：%d MB，待上传：%.4f MB，总限制：%d MB）",
                        sizeNowMB, totalFileSizeMB, limitSizeMB
                );
                sendErrorResponse(out, response, HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, message);
                return;
            }

            // 验证通过（包括空文件）
            JSONObject res = new JSONObject();
            res.put("status", "allow");
            res.put("message", "可以上传");
            out.write(res.toString());

        } catch (Exception e) {
            sendErrorResponse(out, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器处理错误：" + e.getMessage());
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    private void sendErrorResponse(PrintWriter out, HttpServletResponse response,
                                   int status, String message) {
        response.setStatus(status);
        JSONObject res = new JSONObject();
        res.put("status", "error");
        res.put("message", message);
        out.write(res.toString());
    }
}
