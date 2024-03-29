package org.howard1209a.user.controller;

import org.howard1209a.user.mapper.UserMapper;
import org.howard1209a.user.pojo.User;
import org.howard1209a.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/info")
public class InfoController {
    @Autowired
    private UserService userService;

    @GetMapping("/id")
    public User queryUserById(@RequestParam("userId") Long userId) {
        return userService.queryUserById(userId);
    }
}
