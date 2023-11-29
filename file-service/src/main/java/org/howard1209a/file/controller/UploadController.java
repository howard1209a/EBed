package org.howard1209a.file.controller;

import lombok.extern.slf4j.Slf4j;
import org.howard1209a.file.pojo.dto.Response;
import org.howard1209a.file.service.ImgUploadService;
import org.howard1209a.file.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/file/upload")
@Slf4j
public class UploadController {
    @Autowired
    private ImgUploadService imgUploadService;

    @PostMapping("/img")
    public Response<String> imgUpload(MultipartFile[] images, HttpServletRequest request) {
        Cookie cookie= Utils.getCookie("session",request);
        imgUploadService.saveUploadedImg(images,cookie.getValue());


        return new Response<>(true,"上传成功");
    }

    @PostMapping("/img/description/all")
    public Response<String> imgDescriptionAllUpload(@RequestBody List<String> descriptions) {
        return new Response<>(true,"上传成功");
    }
}
