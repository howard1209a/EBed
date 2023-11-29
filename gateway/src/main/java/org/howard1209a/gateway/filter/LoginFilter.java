package org.howard1209a.gateway.filter;

import org.howard1209a.gateway.pojo.UserState;
import org.howard1209a.gateway.util.RedisUtil;
import org.howard1209a.gateway.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.howard1209a.gateway.constant.GatewayConstant.USER_STATE_KEY;

@Component
public class LoginFilter implements GlobalFilter {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 以下前缀放行
        List<String> excludeUrl = Arrays.asList(
                "http://localhost:10010/static/login.html",
                "http://localhost:10010/static/register.html",
                "http://localhost:10010/user/login",
                "http://localhost:10010/user/register");
        for (String prefix : excludeUrl) {
            if (request.getURI().toString().startsWith(prefix)) {
                return chain.filter(exchange);
            }
        }
        HttpCookie cookie = Utils.getCookie("session", request);
        if (cookie != null) {
            UserState userState = redisUtil.getObject(USER_STATE_KEY + cookie.getValue(), UserState.class);
            if (userState.isLogin()) {
                return chain.filter(exchange);
            }
        }

        String url = "http://localhost:10010/static/login.html";
        ServerHttpResponse response = exchange.getResponse();
        //303状态码表示由于请求对应的资源存在着另一个URI，应使用GET方法定向获取请求的资源
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set(HttpHeaders.LOCATION, url);
        return response.setComplete();
    }
}
