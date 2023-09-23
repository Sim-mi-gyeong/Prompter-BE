package com.prompter.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prompter.common.CustomResponseEntity;
import com.prompter.controller.request.KeywordRequest;
import com.prompter.controller.response.KeywordResponse;
import com.prompter.controller.response.ResultResponse;
import com.prompter.controller.response.StreamResultResponse;
import com.prompter.controller.response.SummaryResponse;
import com.prompter.service.CrawlingService;

import com.prompter.service.TextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

@RequiredArgsConstructor
@RequestMapping("/crawling")
@RestController
public class CrawlingController {

    private final CrawlingService crawlingService;
    private final TextService textService;

//    @GetMapping("/test")
//    public CustomResponseEntity<CrawlingResponse> testCrawling(@RequestParam(value = "url") String url) throws JSONException {
//        return CustomResponseEntity.success(crawlingService.process(url));
//    }
//
//    @GetMapping("/naver")
//    public CustomResponseEntity<CrawlingResponse> naverCrawling() throws JSONException {
//        return CustomResponseEntity.success(crawlingService.processNaver());
//    }
//
//    @GetMapping("/google")
//    public CustomResponseEntity<CrawlingResponse> googleCrawling() throws JSONException {
//        return CustomResponseEntity.success(crawlingService.processGoogle());
//    }

    /*
    @GetMapping
	public Mono<CustomResponseEntity<ContentsControlResponse>> get(
		@RequestHeader(value = EXTERNAL_AUTH_HEADER_NAME) String ppsn,
		@RequestHeader(value = MVERSE_REQUESTED_FROM, required = false) String requestedFrom,
		ImsProfileInfo imsProfileInfo,
		Country country
	) {
		return contentsControlService.get(ppsn, imsProfileInfo, country, requestedFrom)
			.map(CustomResponseEntity::success);
	}
     */

    /*
    @GetMapping("/data")
    public Flux<ServerSentEvent<String>> getData() {

        Mono<String> firstResponse = Mono.just("첫번째 응답입니다😆");
        Mono<String> secondResponse = Mono.just("두번째 응답입니다😎");

        Flux<ServerSentEvent<String>> responseStream = Flux.concat(
                firstResponse.map(data -> ServerSentEvent.<String>builder().data(data).build()),
                secondResponse.delayElement(Duration.ofSeconds(5)).map(data -> ServerSentEvent.<String>builder().data(data).build())
        );

        return responseStream;
    }
     */

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

    @GetMapping(value = "/result/keyword")
    public CustomResponseEntity<KeywordResponse> getKeywordsBySearch(@RequestBody @Valid KeywordRequest request) {
        return CustomResponseEntity.success(textService.getKeywordsBySearch(request));
    }
}
