package com.nicole.util;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class CookieUtil {

    /**
     * @param response 携带cookie的响应
     * @param name     cookie名
     * @param value    cookie值
     * @param maxAge   cookie的最大存在时间
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");

        /**
         * Cookie保存中文
         *  Cookie的name和value都是不能保存中文的，但可以先把中文转换成URL编码，然后在保存到Cookie的name和value中。
         */
        try {
            cookie.setValue(URLEncoder.encode(value, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.addCookie(cookie);
    }

    /**
     * 根据Cookie名称得到Cookie对象，不存在该对象则返回Null
     * @param cookies
     * @param name
     * @return
     */
    public static Cookie getCookie(Cookie[] cookies, String name) {
        if (cookies == null) {
            return null;
        } else {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
