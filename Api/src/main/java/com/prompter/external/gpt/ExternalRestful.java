package com.prompter.external.gpt;

import com.prompter.external.gpt.dto.response.papago.PapagoTranslationErrorResponse;
import com.prompter.external.gpt.dto.response.papago.PapagoTranslationException;
import com.prompter.external.gpt.dto.response.papago.PapagoTranslationResponse;
import com.prompter.external.gpt.dto.response.search.SearchDictionaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prompter.external.gpt.dto.request.gpt.OpenAiApiTextSummaryRequest;
import com.prompter.external.gpt.dto.response.gpt.OpenAiApiResultResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalRestful {

    private final WebClient openAiApiWebClient;
    private final WebClient papagoApiWebClient;
    private final WebClient searchApiWebClient;

    private final WebClient koWikipediaApiWebClient;
    private final WebClient enWikipediaApiWebClient;

    /**
     * 텍스트 관련
     */
    public OpenAiApiResultResponse getSummary(String url, int type) {
        return openAiApiWebClient
                .mutate()
                .build()
                .post()
                .uri("/summary")
                .bodyValue(OpenAiApiTextSummaryRequest.of(url, type))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException())
                )
                .bodyToMono(OpenAiApiResultResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new OpenAiApiResultResponse());
    }

    public Flux<OpenAiApiResultResponse> getSummaryByStream(String url, int type) throws JsonProcessingException {
        return openAiApiWebClient
//                .mutate()
//                .build()
                .post()
                .uri("/summarysse")
                .bodyValue(OpenAiApiTextSummaryRequest.of(url, type))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(OpenAiApiResultResponse.class)
                .flatMap(
                        Flux::just
                );
    }

    /*
    public Mono<ProfileCoreContentsControlResponse> getContentsControl(final String ppsn) {
		return profileCoreApiWebClient.get()
			.uri(uriBuilder -> uriBuilder.path("/contents-control/{ppsn}").build(ppsn))
			.retrieve()
			.bodyToMono(WrapperContentsControlResponse.class)
			.flatMap(e -> {
				if (e.getCode() != Result.OK.getCode()) {
					return Mono.error(new ProfileException(Result.valueOf(e.getCode()), e.getMessage()));
				}

				if (e.getData() == null) {
					return Mono.empty();
				}

				return Mono.just(e.getData());
			});
	}
     */

    /**
     * Papago 번역 API 호출
     */
    public PapagoTranslationResponse translateText(MultiValueMap<String, String> map) {
        return papagoApiWebClient
                .mutate()
                .build()
                .post()
                .uri("")
                .bodyValue(map)
                .retrieve()
                .onStatus(
                        httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                        clientResponse -> Mono.error(new PapagoTranslationException((PapagoTranslationErrorResponse) clientResponse))

                )
                .bodyToMono(PapagoTranslationResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new PapagoTranslationResponse());
    }

    /**
     * 네이버 검색 - 백과사전 API 호출
     */
    public SearchDictionaryResponse searchByDictionary(String query) {
        return searchApiWebClient.mutate()
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("")
                        .queryParam("query", query)
                        .queryParam("display", 1)
                        .build()
                )
                .retrieve()
                .onStatus(
                        httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException())
                )
                .bodyToMono(SearchDictionaryResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new SearchDictionaryResponse());
    }

    public Flux<SearchDictionaryResponse> searchByDictionaryStream(String query) {
        return searchApiWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("")
                        .queryParam("query", query)
                        .queryParam("display", 1)
                        .build()
                )
                .retrieve()
                .bodyToFlux(SearchDictionaryResponse.class)
                .flatMap(Flux::just);
    }
}