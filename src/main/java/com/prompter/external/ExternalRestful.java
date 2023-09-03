package com.prompter.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalRestful {

    private final WebClient openAiApiWebClient;

    public OpenAiApiSummaryResponse getTextSummary(String text) {
        var result = openAiApiWebClient.post()
                .uri("/summary")
                .bodyValue(OpenAiApiSummaryRequest.from(text))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(OpenAiApiSummaryResponse.class);
        return result.block();
    }

    public OpenAiApiClassifyResponse checkAds(String text) {
        var result = openAiApiWebClient.post()
                .uri("/adclassify")
                .bodyValue(OpenAiApiSummaryRequest.from(text))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(OpenAiApiClassifyResponse.class);
//                .exchangeToMono(clientResponse -> {
//                    return clientResponse.bodyToMono(Boolean.class);
//                });
        return result.block();
    }
}