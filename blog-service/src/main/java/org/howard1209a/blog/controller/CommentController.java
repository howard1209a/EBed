package org.howard1209a.blog.controller;

import org.howard1209a.blog.pojo.Comment;
import org.howard1209a.blog.pojo.dto.CommentDto;
import org.howard1209a.blog.pojo.dto.Response;
import org.howard1209a.blog.service.CommentService;
import org.howard1209a.blog.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/blog/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("publish")
    public void publishOneComment(@RequestBody Comment comment, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        commentService.publishOneComment(cookie.getValue(), comment);
    }

    @GetMapping("/all")
    public Response<List<CommentDto>> queryAllCommentForOneBlog(@RequestParam Long blogId, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        return commentService.queryAllCommentForOneBlog(cookie.getValue(), blogId);
    }

    @GetMapping("like")
    public void likeOneComment(@RequestParam Long commentId, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        commentService.likeOneComment(cookie.getValue(), commentId);
    }

    @GetMapping("unlike")
    public void unlikeOneComment(@RequestParam Long commentId, HttpServletRequest request) {
        Cookie cookie = Utils.getCookie("session", request);
        commentService.unlikeOneComment(cookie.getValue(), commentId);
    }
}
