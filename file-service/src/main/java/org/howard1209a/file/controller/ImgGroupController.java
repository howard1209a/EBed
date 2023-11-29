package org.howard1209a.file.controller;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.howard1209a.file.mapper.ImgGroupMapper;
import org.howard1209a.file.pojo.ImgGroup;
import org.howard1209a.file.pojo.dto.Response;
import org.howard1209a.file.service.ImgGroupService;
import org.howard1209a.file.util.SnowflakeIdUtils;
import org.howard1209a.file.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/file/imgGroup")
@Slf4j
public class ImgGroupController {
    @Autowired
    private ImgGroupService imgGroupService;

    @GetMapping("create")
    public Response<String> createImgGroup(@RequestParam("imgGroupName") String imgGroupName, @RequestParam(value = "userId", required = false) Long userId, HttpServletRequest httpServletRequest) {
        Cookie cookie = Utils.getCookie("session", httpServletRequest);
        return imgGroupService.saveGroup(imgGroupName, userId, cookie.getValue());
    }

    @GetMapping("info")
    public Response<List<String>> imgGroupInfo(HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        return imgGroupService.getAllImgGroupName(cookie.getValue());
    }

    @GetMapping("delete")
    public void deleteImgGroup(@RequestParam("imgGroupName") String imgGroupName, HttpServletRequest httpServletRequest) {
        Cookie cookie = Utils.getCookie("session", httpServletRequest);
        imgGroupService.deleteGroup(imgGroupName, cookie.getValue());
    }
}
