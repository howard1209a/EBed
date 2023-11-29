package org.howard1209a.gateway.util;

import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Utils {
    public static HttpCookie getCookie(String name, ServerHttpRequest serverHttpRequest) {
        MultiValueMap<String, HttpCookie> map = serverHttpRequest.getCookies();
        Collection<List<HttpCookie>> values = map.values();
        List<HttpCookie> cookies = new ArrayList<>();
        Iterator<List<HttpCookie>> iterator = values.iterator();
        while (iterator.hasNext()) {
            cookies.add(iterator.next().get(0));
        }
        if (cookies.size() > 0) {
            for (HttpCookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
