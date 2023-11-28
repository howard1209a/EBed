package org.howard1209a.file.controller;

import lombok.extern.slf4j.Slf4j;
import org.howard1209a.file.pojo.dto.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file/upload")
@Slf4j
public class UploadController {
    @PostMapping("/img")
    public Response<String> imgUpload(MultipartFile[] images) {
        // 处理每个上传的文件
        for (MultipartFile image : images) {
            // 保存文件到服务器或云存储
            // 这里只是演示，实际上需要根据项目需求进行处理

            // 获取文件名
            String fileName = image.getOriginalFilename();
            System.out.println(fileName);

            // 生成文件保存路径，这里假设保存在项目的upload目录下
            String filePath = "upload/" + fileName;

            // 将文件保存到指定路径
            // 这里可以使用 FileUtils.copyInputStreamToFile() 方法或其他文件操作方式
            // 例如：FileUtils.copyInputStreamToFile(image.getInputStream(), new File(filePath));
        }
        return new Response<>(true,"上传成功");
    }

    @PostMapping("/img/description/all")
    public Response<String> imgDescriptionAllUpload(@RequestBody List<String> descriptions) {
        return null;
    }
}
