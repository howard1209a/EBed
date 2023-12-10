package org.howard1209a.user.controller;

import org.howard1209a.user.pojo.User;
import org.howard1209a.user.pojo.dto.ExpDto;
import org.howard1209a.user.pojo.dto.Response;
import org.howard1209a.user.service.UserService;
import org.howard1209a.user.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/user/common")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/profilePhoto/upload")
    public void uploadProfilePhoto(@RequestParam("avatar") MultipartFile multipartFile, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        userService.saveProfilePhoto(cookie.getValue(), multipartFile);
    }

    @GetMapping("/profilePhoto/download")
    public void downloadProfilePhoto(@RequestParam(value = "userId",required = false) Long userId, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = Utils.getCookie("session", request);
        userService.downloadProfilePhoto(userId,cookie.getValue(), request, response);
    }

    @GetMapping("/userName/update")
    public void updateUserName(String userName, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        userService.updateUserName(cookie.getValue(), userName);
    }

    @GetMapping("/exp/select")
    public Response<ExpDto> queryExpByNextProfileUserId(HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        return userService.queryExpByNextProfileUserId(cookie.getValue());
    }

    @GetMapping("/exp/add")
    public void addExp(Long userId, String addExpType) {
        userService.addExp(userId, addExpType);
    }

    @PostMapping("/selfIntroduction/update")
    public void updateSelfIntroduction(@RequestBody String selfIntroduction, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        userService.updateSelfIntroduction(cookie.getValue(), selfIntroduction);
    }

    @GetMapping("/query")
    public Response<User> queryOneUserByNextProfileUserId(HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        return userService.queryOneUserByNextProfileUserId(cookie.getValue());
    }

    @GetMapping("/follow")
    public void follow(Long followedUserId, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        userService.follow(cookie.getValue(), followedUserId);
    }

    @GetMapping("/unfollow")
    public void unFollow(Long unFollowedUserId, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        userService.unFollow(cookie.getValue(), unFollowedUserId);
    }

    @GetMapping("/nextProfileUserId")
    public void setNextProfileUserId(Long nextProfileUserId, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        userService.setNextProfileUserId(cookie.getValue(), nextProfileUserId);
    }

    @GetMapping("/loginUser")
    public Response<User> queryLoginUser(HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        return userService.queryLoginUser(cookie.getValue());
    }
}
