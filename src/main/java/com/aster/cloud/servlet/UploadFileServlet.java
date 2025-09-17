package com.aster.cloud.servlet;
import com.aster.cloud.utils.FileManager;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Collection;

@WebServlet("/uploadFile")
@MultipartConfig
public class UploadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("经过UploadServlet的请求 = " + request.getRequestURI());
        // 设置响应类型为JSON
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            // 获取上传路径
            String pathToUpload = request.getParameter("path_to_upload");
            if (pathToUpload == null || pathToUpload.trim().isEmpty()) {
                sendErrorResponse(out, response, HttpServletResponse.SC_BAD_REQUEST,
                        "上传路径不能为空");
                return;
            }

            // 路径权限验证
            String userDirectory = (String) request.getSession().getAttribute("user_directory");
            if (userDirectory == null || !pathToUpload.startsWith(userDirectory.replace("\\", "/"))) {
                sendErrorResponse(out, response, HttpServletResponse.SC_FORBIDDEN,
                        "无权限上传至该路径");
                out.flush();
                out.close();
                forceCloseConnection(request, response);
                return;
            }

            // 二次容量验证
            long sizeNowMB = FileManager.getSizeNow(request);
            long limitSizeMB = FileManager.getLimitSize(request);

            // 计算实际上传文件总大小
            Collection<Part> parts = request.getParts();
            long totalUploadSizeByte = 0;
            for (Part part : parts) {
                String fileName = part.getSubmittedFileName();
                if (fileName != null && !fileName.isEmpty()) {
                    totalUploadSizeByte += part.getSize();
                }
            }

            double totalUploadSizeMB = totalUploadSizeByte / (1024.0 * 1024);
            if (sizeNowMB + totalUploadSizeMB >= limitSizeMB) {
                String message = String.format(
                        "文件大小超过存储限制（已用：%d MB，待上传：%.2f MB，总限制：%d MB）",
                        sizeNowMB, totalUploadSizeMB, limitSizeMB
                );
                sendErrorResponse(out, response, HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                        message);
                out.flush();
                out.close();
                forceCloseConnection(request, response);
                return;
            }

            // 执行文件上传
            boolean success = FileManager.uploadFile(request, response, pathToUpload);
            if (success) {
                JSONObject res = new JSONObject();
                res.put("status", "success");
                res.put("message", "上传成功");
                out.write(res.toString());
            } else {
                sendErrorResponse(out, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "文件写入失败");
            }

        } catch (Exception e) {
            sendErrorResponse(out, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "服务器处理错误：" + e.getMessage());
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    // 发送错误响应
    private void sendErrorResponse(PrintWriter out, HttpServletResponse response,
                                   int status, String message) {
        response.setStatus(status);
        JSONObject res = new JSONObject();
        res.put("status", "error");
        res.put("message", message);
        out.write(res.toString());
    }

    // 强制关闭连接
    private void forceCloseConnection(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 反射获取Socket并关闭（Tomcat环境）
            Field requestField = request.getClass().getDeclaredField("request");
            requestField.setAccessible(true);
            Object tomcatRequest = requestField.get(request);

            Field socketField = tomcatRequest.getClass().getDeclaredField("socket");
            socketField.setAccessible(true);
            Object socketWrapper = socketField.get(tomcatRequest);

            Field socketImplField = socketWrapper.getClass().getDeclaredField("socket");
            socketImplField.setAccessible(true);
            Socket socket = (Socket) socketImplField.get(socketWrapper);

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            try {
                request.getInputStream().close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
