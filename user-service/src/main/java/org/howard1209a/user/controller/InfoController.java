package org.howard1209a.user.controller;

import org.howard1209a.user.mapper.InfoMapper;
import org.howard1209a.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/info")
public class InfoController {
    @Autowired
    private InfoMapper infoMapper;

    @GetMapping("/id")
    public User queryInfoById(@RequestParam("userId") Long userId) {
        return infoMapper.queryUserById(userId);
    }
}
