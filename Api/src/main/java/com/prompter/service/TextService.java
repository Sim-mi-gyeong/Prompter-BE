package com.prompter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prompter.controller.request.KeywordRequest;
import com.prompter.controller.response.KeywordResponse;
import com.prompter.controller.response.StreamResultResponse;
import com.prompter.external.gpt.ExternalRestful;
import com.prompter.external.gpt.dto.response.gpt.OpenAiApiResultResponse;
import com.prompter.controller.response.ResultResponse;
import com.prompter.external.gpt.dto.response.search.SearchDictionaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;

import org.json.JSONException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TextService {

    private final ExternalRestful externalRestful;

    private static final Double PERCENT_GPT = 39.5;
    private static final Double PERCENT_RULE_BASE = 59.5;

    public List<ResultResponse.Keyword> getKeywords(String tags) {
        return Arrays.stream(tags.split(","))
                .map(this::createKeyword).collect(Collectors.toList());
    }

    public List<Flux<StreamResultResponse.Keyword>> getKeywordsByStream(String tags) {
        return Arrays.stream(tags.split(","))
                .map(this::createKeywordByStream)
                .collect(Collectors.toList());
    }

    public ResultResponse getSummaryAndAnalyzedText(String url, int type, String language) throws JSONException {

        OpenAiApiResultResponse clientResponse = getSummaryResponse(url, type);

        List<ResultResponse.Keyword> keywords = getKeywords(clientResponse.getTags());

        // Rule Base 광고 분류 적용
        boolean classifyAdsYn = clientResponse.getAdYn().equals("O");
        return ResultResponse.of(clientResponse.getTitle(),
                clientResponse.getSummary(), Arrays.asList(clientResponse.getTags().split(",")), keywords,
                calculatePercentByAi(classifyAdsYn) + calculatePercentByRule(clientResponse.getContent())
            );
    }

    public double calculatePercentByAi(boolean flag) {
        if (flag) return PERCENT_GPT;
        else return 0.0;
    }

    // [‘소정의‘, ‘원고료‘, ‘지원받아‘, ‘업체로부터‘, ‘업체에게‘, ‘광고‘, ‘유료광고‘, ‘협찬’]
    public double calculatePercentByRule(String content) {
        if (content.contains("소정의") || content.contains("원고료") || content.contains("수수료") || content.contains("지원받아") || content.contains("업체로부터")
            || content.contains("업체에게") || content.contains("광고") || content.contains("유료광고") || content.contains("협찬")
        ) {
            return PERCENT_RULE_BASE;
        }
        return 0.0;
    }

    @Async("sampleExecutor")
    public OpenAiApiResultResponse getSummaryResponse(String url, int type) {
        return externalRestful.getSummary(url, type);
    }

    public Flux<StreamResultResponse> getSummaryAndAnalyzedTextStream(String url, int type, String language) throws JsonProcessingException {

        return externalRestful.getSummaryByStream(url, type)
                .flatMap(
                        response -> Flux.just(
                                StreamResultResponse.of(
                                        response.getTitle(), response.getSummary()
                                        , Arrays.asList(response.getTags().split(",")), getKeywordsByStream(response.getTags())
                                        , calculatePercentByAi(response.getAdYn().equals("O")) + calculatePercentByRule(response.getContent())
                                )
                            )
                );
    }

    /**
     * 네이버 검색 - 백과사전
     */
    public ResultResponse.Keyword createKeyword(String tag) {
        SearchDictionaryResponse searchDictionaryResponse = searchByDictionary(tag);
        return new ResultResponse.Keyword(
                tag.replace(" ", ""),
                getDescription(searchDictionaryResponse),
                getLink(searchDictionaryResponse)
        );
    }

    public Flux<StreamResultResponse.Keyword> createKeywordByStream(String tag) {
        return externalRestful.searchByDictionaryStream(tag)
                .flatMap(
                        response -> {
                            return Flux.just(
                                    new StreamResultResponse.Keyword(
                                        tag.replace(" ", ""),
                                        getDescription(response),
                                        getLink(response)
                                    )
                            );
                        }
                );
    }

    public SearchDictionaryResponse searchByDictionary(String keyword) {
        return externalRestful.searchByDictionary(keyword);
    }

    public String getDescription(SearchDictionaryResponse response) {
        if (!ObjectUtils.isEmpty(response.getItems().stream())) {
            return response.getItems().stream().findFirst().get().getDescription();
        }
        return "";
    }

    public String getLink(SearchDictionaryResponse response) {
        if (!ObjectUtils.isEmpty(response.getItems().stream())) {
            return response.getItems().stream().findFirst().get().getLink();
        }
        return "";
    }

    public KeywordResponse getKeywordsBySearch(KeywordRequest request) {

        return new KeywordResponse(
                request.getTags().stream()
                        .map(tag -> new KeywordResponse.Keyword(
                                        createKeyword(tag.replace(" ", "")))
                            ).collect(Collectors.toList()
                        )
        );
    }
}