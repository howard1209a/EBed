package org.howard1209a.blog.feign;

import org.howard1209a.blog.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("userservice")
public interface UserClient {
    @GetMapping("/user/info/id")
    public User queryInfoById(@RequestParam("userId") Long userId);
}
