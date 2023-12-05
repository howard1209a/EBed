package org.howard1209a.search.controller;

import org.howard1209a.search.pojo.BlogDoc;
import org.howard1209a.search.pojo.dto.BlogSearchDto;
import org.howard1209a.search.pojo.dto.Response;
import org.howard1209a.search.service.BlogESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blog/es")
public class BlogESController {
    @Autowired
    private BlogESService blogESService;

    @PostMapping("search")
    public Response<List<BlogDoc>> search(@RequestBody BlogSearchDto blogSearchDto) {
        blogESService.search(blogSearchDto)
    }
}
