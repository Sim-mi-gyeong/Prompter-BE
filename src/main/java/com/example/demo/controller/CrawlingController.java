package com.example.demo.controller;

import com.example.demo.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/crawling")
@RestController
public class CrawlingController {

    private final CrawlingService crawlingService;

    @GetMapping("/test")
    public JSONObject test2() throws JSONException {
        return crawlingService.process();
    }

}
