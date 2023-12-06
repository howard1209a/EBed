package org.howard1209a.search.controller;

import org.howard1209a.search.pojo.BlogDoc;
import org.howard1209a.search.pojo.dto.BlogSearchDto;
import org.howard1209a.search.pojo.dto.Response;
import org.howard1209a.search.service.BlogESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search/blog")
public class BlogESController {
    @Autowired
    private BlogESService blogESService;

    @PostMapping("info")
    public Response<List<BlogDoc>> searchInfo(@RequestBody BlogSearchDto blogSearchDto) {
        List<BlogDoc> blogDocs = blogESService.searchInfo(blogSearchDto);
        return new Response<>(true, blogDocs);
    }

    @GetMapping("prefix")
    public Response<List<String>> searchPrefix(@RequestParam String prefix) {
        List<String> suggestions = blogESService.searchPrefix(prefix);
        return new Response<>(true, suggestions);
    }
}
