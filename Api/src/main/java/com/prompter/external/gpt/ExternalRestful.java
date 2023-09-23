package com.prompter.external.gpt;

import com.prompter.common.LanguageCode;
import com.prompter.external.gpt.dto.response.papago.PapagoTranslationErrorResponse;
import com.prompter.external.gpt.dto.response.papago.PapagoTranslationException;
import com.prompter.external.gpt.dto.response.papago.PapagoTranslationResponse;
import com.prompter.external.gpt.dto.response.wikipedia.wikipediaApiResponse;
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

import java.util.Locale;


@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalRestful {

    private final WebClient openAiApiWebClient;
    private final WebClient papagoApiWebClient;
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

    public Flux<OpenAiApiResultResponse> getTextSummaryByStream(String text, int type) throws JsonProcessingException {
        Flux<OpenAiApiResultResponse> streamText = openAiApiWebClient
                .mutate()
                .build()
                .post()
                .uri("/summary")
                .bodyValue(OpenAiApiTextSummaryRequest.of(text, type))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(OpenAiApiResultResponse.class);

        return streamText;
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
     * 위키피디아 관련 API 호출
     */
    public wikipediaApiResponse getWikipediaContent(String keyword, String language) {
        log.info("language : {}", language);
        if (language.equals(LanguageCode.EN.getDesc())) {
            return getEnWikipediaContent(keyword);
        } else {
            return getKoWikipediaContent(keyword);
        }
    }

    public wikipediaApiResponse getKoWikipediaContent(String keyword) {

        return koWikipediaApiWebClient.mutate()
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("")
                        .queryParam("format", "json")
                        .queryParam("action", "query")
                        .queryParam("prop", "extracts")
                        .queryParam("exintro", "")
                        .queryParam("explaintext", "")
                        .queryParam("titles", keyword)
                        .build()
                )
                .retrieve()
                .onStatus(
                        httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException())
                )
                .bodyToMono(wikipediaApiResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new wikipediaApiResponse());
    }

    public wikipediaApiResponse getEnWikipediaContent(String keyword) {

        return enWikipediaApiWebClient.mutate()
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("")
                        .queryParam("format", "json")
                        .queryParam("action", "query")
                        .queryParam("prop", "extracts")
                        .queryParam("exintro", "")
                        .queryParam("explaintext", "")
                        .queryParam("titles", keyword.toLowerCase(Locale.ENGLISH))
                        .build()
                )
                .retrieve()
                .onStatus(
                        httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException())
                )
                .bodyToMono(wikipediaApiResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new wikipediaApiResponse());
    }
}