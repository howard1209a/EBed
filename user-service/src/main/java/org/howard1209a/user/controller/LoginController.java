package org.howard1209a.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.howard1209a.user.pojo.dto.LoginDto;
import org.howard1209a.user.pojo.dto.Response;
import org.howard1209a.user.service.LoginService;
import org.howard1209a.user.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user/login")
@Slf4j
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("")
    public Response<String> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        return loginService.loginCheck(loginDto, cookie.getValue());
    }
}
