package com.aster.cloud.utils;

import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HttpSessionManager
 * 管理用户的 HttpSession，可以通过用户名获取 session
 */
public class HttpSessionManager {

    // 线程安全的 Map，用于存储 userName -> HttpSession
    private static final Map<String, HttpSession> sessionMap = new ConcurrentHashMap<>();

    /**
     * 添加或更新用户 session
     * @param userName 用户名
     * @param session 该用户的 HttpSession
     */
    public static void addSession(String userName, HttpSession session) {
        if (userName != null && session != null) {
            sessionMap.put(userName, session);
        }
    }

    /**
     * 获取指定用户的 session
     * @param userName 用户名
     * @return HttpSession，如果不存在返回 null
     */
    public static HttpSession getSession(String userName) {
        if (userName == null) return null;
        return sessionMap.get(userName);
    }

    /**
     * 移除指定用户的 session
     * @param userName 用户名
     */
    public static void removeSession(String userName) {
        if (userName != null) {
            sessionMap.remove(userName);
        }
    }

    /**
     * 使指定用户的 session 失效
     * @param userName 用户名
     */
    public static void invalidateSession(String userName) {
        HttpSession session = getSession(userName);
        if (session != null) {
            try {
                session.invalidate();
            } catch (IllegalStateException ignored) {
                // session 已经失效，可以忽略
            }
            removeSession(userName);
        }
    }
}
