package com.prompter.controller;

import com.prompter.common.CustomResponseEntity;
import com.prompter.dto.response.CrawlingResponse;
import com.prompter.dto.response.ResultResponse;
import com.prompter.dto.response.SummaryResponse;
import com.prompter.service.CrawlingService;

import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/crawling")
@RestController
public class CrawlingController {

    private final CrawlingService crawlingService;
    private final TextService textService;

    @GetMapping("/test")
    public CustomResponseEntity<CrawlingResponse> testCrawling() throws JSONException {
        return CustomResponseEntity.success(crawlingService.process2());
    }

    @GetMapping("/naver")
    public CustomResponseEntity<CrawlingResponse> naverCrawling() throws JSONException {
        return CustomResponseEntity.success(crawlingService.processNaver());
    }

    @GetMapping("/google")
    public CustomResponseEntity<CrawlingResponse> googleCrawling() throws JSONException {
        return CustomResponseEntity.success(crawlingService.processGoogle());
    }

    /**
     * URL 에 해당하는 사이트 텍스트 내용 리턴
     */
    @GetMapping
    public CustomResponseEntity<CrawlingResponse> getTextContent(@RequestParam(value = "url") String url) throws JSONException {
        return CustomResponseEntity.success(textService.getTextContent(url));
    }

    /**
     * URL 에 해당하는 사이트 텍스트 내용 요약 결과
     */
    @GetMapping("/summary")
    public CustomResponseEntity<SummaryResponse> getSummaryText(@RequestParam(value = "url") String url) throws JSONException {
        return CustomResponseEntity.success(textService.getSummaryText(url));
    }

    /**
     * URL 에 해당하는 사이트 텍스트 내용 요약 및 분석 결과
     */
    @GetMapping("/result")
    public CustomResponseEntity<ResultResponse> getSummaryAndAnalyzedText(@RequestParam(value = "url") String url) throws JSONException {
        return CustomResponseEntity.success(textService.getSummaryAndAnalyzedText(url));
    }
}
