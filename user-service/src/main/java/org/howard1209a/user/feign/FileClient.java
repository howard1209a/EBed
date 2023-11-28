package org.howard1209a.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("fileservice")
public interface FileClient {
    @GetMapping("/file/imgGroup/create")
    void createImgGroup(@RequestParam("imgGroupName") String imgGroupName, @RequestParam("userId") Long userId);
}
