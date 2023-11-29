package org.howard1209a.file.controller;

import lombok.extern.slf4j.Slf4j;
import org.howard1209a.file.pojo.dto.Response;
import org.howard1209a.file.service.ImgService;
import org.howard1209a.file.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file/img")
@Slf4j
public class ImgController {
    @Autowired
    private ImgService imgService;

    @GetMapping("/count")
    public Response<Integer> countImgForGroup(@RequestParam("groupName") String groupName, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        return imgService.countImgForGroup(groupName, cookie.getValue(), request);
    }
}
