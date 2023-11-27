package org.howard1209a.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.howard1209a.user.pojo.dto.RegisterDto;
import org.howard1209a.user.pojo.dto.Response;
import org.howard1209a.user.service.RegisterService;
import org.howard1209a.user.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/user/register")
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    @GetMapping("identifyImage")
    public void identifyImage(HttpServletResponse response, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        registerService.identifyImage(response, cookie.getValue());
    }

    @GetMapping("session")
    public void session(HttpServletResponse response, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        if (cookie == null) {
            registerService.createSession(response);
        }
    }

    @PostMapping("register")
    public Response<String> login(@RequestBody RegisterDto registerDto, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        return registerService.checkAndRegister(registerDto, cookie.getValue());
    }
}
