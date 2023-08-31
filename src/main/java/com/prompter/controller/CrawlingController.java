package com.prompter.controller;

import com.prompter.common.CustomResponseEntity;
import com.prompter.dto.response.CrawlingResponse;
import com.prompter.service.CrawlingService;

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

    @GetMapping("/test1")
    public JSONObject test() throws JSONException {
        return crawlingService.process1();
    }

    @GetMapping("/test2")
    public CustomResponseEntity<CrawlingResponse> testCrawling() throws JSONException {
        return CustomResponseEntity.success(crawlingService.process2());
    }
}
