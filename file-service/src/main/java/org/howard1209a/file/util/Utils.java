package org.howard1209a.file.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class Utils {
    public static Cookie getCookie(String name, HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
