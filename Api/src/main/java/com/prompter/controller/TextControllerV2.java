package com.prompter.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prompter.common.CustomResponseEntity;
import com.prompter.controller.response.StreamResultResponse;
import com.prompter.service.CrawlingService;
import com.prompter.service.TextService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "요약/광고 분류/추천 관련 API V2", description = "Checky 서비스 메인 API V2")
@RequiredArgsConstructor
@RequestMapping("/v2/crawling")
@RestController
public class TextControllerV2 {

    private final TextService textService;

    /**
     * URL 에 해당하는 사이트 텍스트 내용 요약 및 분석 결과
     * @return
     */
    @GetMapping(value = "/result/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CustomResponseEntity<StreamResultResponse>> getSummaryAndAnalyzedTextStream(
            @RequestParam(value = "url") String url, @RequestParam(value = "type") int type,
            @RequestParam(value = "language", required = false) String language) throws JSONException, JsonProcessingException {

        return textService.getSummaryAndAnalyzedTextStream(url, type, language)
                .map(CustomResponseEntity::success);
    }
}


