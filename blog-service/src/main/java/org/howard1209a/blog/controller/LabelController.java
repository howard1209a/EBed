package org.howard1209a.blog.controller;

import org.howard1209a.blog.pojo.dto.Response;
import org.howard1209a.blog.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blog/label")
public class LabelController {
    @Autowired
    private LabelService labelService;

    @GetMapping("/all")
    public Response<List<String>> allLabel() {
        return new Response<>(true, labelService.queryAllLabel());
    }
}
