package org.howard1209a.blog.controller;

import org.howard1209a.blog.mapper.LabelMapper;
import org.howard1209a.blog.pojo.Comment;
import org.howard1209a.blog.pojo.dto.BlogDto;
import org.howard1209a.blog.pojo.dto.BlogLoadDto;
import org.howard1209a.blog.pojo.dto.Response;
import org.howard1209a.blog.service.BlogService;
import org.howard1209a.blog.service.LabelService;
import org.howard1209a.blog.util.RedisUtil;
import org.howard1209a.blog.util.SnowflakeIdUtils;
import org.howard1209a.blog.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private LabelService labelService;

    @PostMapping("publish")
    public void publishOneBlog(@RequestBody BlogDto blogDto, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        blogService.publishOneBlog(blogDto, cookie.getValue());
    }

    @GetMapping("browse/refresh")
    public void browseRefresh(HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        blogService.browseRefresh(cookie.getValue());
    }

    @GetMapping("browse/load")
    public Response<List<BlogLoadDto>> browseLoad(Integer loadNum, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        List<BlogLoadDto> blogLoadDtos = blogService.browseLoad(cookie.getValue(), loadNum);
        return new Response<>(true, blogLoadDtos);
    }

    @GetMapping("favorite")
    public void favoriteBlog(Long blogId, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        blogService.favoriteBlog(cookie.getValue(), blogId);
    }

    @GetMapping("unfavorite")
    public void unfavoriteBlog(Long blogId, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        blogService.unfavoriteBlog(cookie.getValue(), blogId);
    }

    @GetMapping("queryOne")
    public Response<BlogLoadDto> queryOneBlog(Long blogId, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        BlogLoadDto blogLoadDto = blogService.queryOneBlogById(cookie.getValue(), blogId);
        return new Response<>(true, blogLoadDto);
    }
}
