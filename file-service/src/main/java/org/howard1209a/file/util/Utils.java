package org.howard1209a.file.util;

import org.howard1209a.file.pojo.ImgGroup;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.howard1209a.file.constant.FileConstant.USER_IMG_STORAGE_DIR;

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

    public static String getImgStoragePath(Long userId) {
        return USER_IMG_STORAGE_DIR + userId + "/";
    }
}
