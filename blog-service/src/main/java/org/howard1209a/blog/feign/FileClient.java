package org.howard1209a.blog.feign;

import org.howard1209a.blog.pojo.Img;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("fileservice")
public interface FileClient {
    @GetMapping("/file/img/info")
    Img queryInfoById(@RequestParam("imgId") Long imgId);
}
