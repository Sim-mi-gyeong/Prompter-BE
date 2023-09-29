package com.prompter.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prompter.common.CustomResponseEntity;
import com.prompter.controller.request.KeywordRequest;
import com.prompter.controller.response.KeywordResponse;
import com.prompter.controller.response.ResultResponse;
import com.prompter.controller.response.StreamResultResponse;
import com.prompter.service.CrawlingService;

import com.prompter.service.TextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RequestMapping("/crawling")
@RestController
public class CrawlingController {

    private final CrawlingService crawlingService;
    private final TextService textService;

    /**
     * URL 에 해당하는 사이트 텍스트 내용 요약 및 분석 결과
     * @return
     */
    @GetMapping("/result")
    public CustomResponseEntity<ResultResponse> getSummaryAndAnalyzedText(
            @RequestParam(value = "url") String url, @RequestParam(value = "type") int type,
            @RequestParam(value = "language", required = false) String language) throws JSONException {
        return CustomResponseEntity.success(textService.getSummaryAndAnalyzedText(url, type, language));
    }

    @GetMapping(value = "/result/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CustomResponseEntity<StreamResultResponse>> getSummaryAndAnalyzedTextStream(
            @RequestParam(value = "url") String url, @RequestParam(value = "type") int type,
            @RequestParam(value = "language", required = false) String language) throws JSONException, JsonProcessingException {

        return textService.getSummaryAndAnalyzedTextStream(url, type, language)
                .map(CustomResponseEntity::success);
    }

    @GetMapping(value = "/result/stream/v2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StreamResultResponse> getSummaryAndAnalyzedTextStreamV2(
            @RequestParam(value = "url") String url, @RequestParam(value = "type") int type,
            @RequestParam(value = "language", required = false) String language) throws JSONException, JsonProcessingException {

        return textService.getSummaryAndAnalyzedTextStream(url, type, language);
    }

    @GetMapping(value = "/result/keyword")
    public CustomResponseEntity<KeywordResponse> getKeywordsBySearch(@RequestBody @Valid KeywordRequest request) {
        return CustomResponseEntity.success(textService.getKeywordsBySearch(request));
    }
}