package org.howard1209a.file.controller;

import lombok.extern.slf4j.Slf4j;
import org.howard1209a.file.pojo.Img;
import org.howard1209a.file.pojo.dto.ImgDto;
import org.howard1209a.file.pojo.dto.ImgModifyDto;
import org.howard1209a.file.pojo.dto.ImgPaginationDto;
import org.howard1209a.file.pojo.dto.Response;
import org.howard1209a.file.service.ImgService;
import org.howard1209a.file.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/file/img")
@Slf4j
public class ImgController {
    @Autowired
    private ImgService imgService;

    @GetMapping("/count")
    public Response<Integer> countImgForGroup(@RequestParam("imgGroupName") String imgGroupName, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        return imgService.countImgForGroup(imgGroupName, cookie.getValue());
    }

    @PostMapping("/pagination")
    public Response<List<ImgDto>> imgPaginationForGroup(@RequestBody ImgPaginationDto imgPaginationDto, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        return imgService.imgPaginationForGroup(imgPaginationDto, cookie.getValue());
    }

    @GetMapping("/download/{imgId}")
    public void downImg(@PathVariable("imgId") String imgId, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = Utils.getCookie("session", request);
        imgService.download(response, imgId, cookie.getValue());
    }

    @PostMapping("/modify")
    public void modifyImg(@RequestBody ImgModifyDto imgModifyDto, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        imgService.modifyImg(imgModifyDto, cookie.getValue());
    }

    @GetMapping("/info")
    public Img queryInfoById(@RequestParam("imgId") Long imgId) {
        Img img = imgService.queryInfoById(imgId);
        return img;
    }
}
