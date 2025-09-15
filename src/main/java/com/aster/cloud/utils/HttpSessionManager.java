package com.aster.cloud.utils;

import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class HttpSessionManager {

    // 线程安全的 Map，用于存储 userName -> HttpSession
    private static final Map<String, HttpSession> sessionMap = new ConcurrentHashMap<>();


    public static void addSession(String userName, HttpSession session) {
        if (userName != null && session != null) {
            sessionMap.put(userName, session);
        }
    }

    public static HttpSession getSession(String userName) {
        if (userName == null) return null;
        return sessionMap.get(userName);
    }


    public static void removeSession(String userName) {
        if (userName != null) {
            sessionMap.remove(userName);
        }
    }


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
