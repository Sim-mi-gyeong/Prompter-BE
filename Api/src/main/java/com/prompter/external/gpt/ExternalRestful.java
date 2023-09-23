package com.prompter.external.gpt;

import com.prompter.external.gpt.dto.response.papago.PapagoTranslationErrorResponse;
import com.prompter.external.gpt.dto.response.papago.PapagoTranslationException;
import com.prompter.external.gpt.dto.response.papago.PapagoTranslationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prompter.external.gpt.dto.request.gpt.OpenAiApiTextSummaryRequest;
import com.prompter.external.gpt.dto.response.gpt.OpenAiApiClassifyResponse;
import com.prompter.external.gpt.dto.response.gpt.OpenAiApiSummaryResponse;
import com.prompter.external.gpt.dto.response.gpt.OpenAiApiTagResponse;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalRestful {

    private final WebClient openAiApiWebClient;
    private final WebClient papagoApiWebClient;

    /**
     * 텍스트 관련
     */
    public String getTextSummary(String text, int type) {
        return openAiApiWebClient
                .mutate()
                .build()
                .post()
                .uri("/summary")
                .bodyValue(OpenAiApiTextSummaryRequest.of(text, type))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class).block();
    }

    public Flux<OpenAiApiSummaryResponse> getTextSummaryByStream(String text, int type) throws JsonProcessingException {
        Flux<OpenAiApiSummaryResponse> streamText = openAiApiWebClient
                .mutate()
                .build()
                .post()
                .uri("/summary")
                .bodyValue(OpenAiApiTextSummaryRequest.of(text, type))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(OpenAiApiSummaryResponse.class);

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

    public OpenAiApiTagResponse getTags(String text, int type) {
        return openAiApiWebClient
                .mutate()
                .build()
                .post()
                .uri("/tagging")
                .bodyValue(OpenAiApiTextSummaryRequest.of(text, type))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(OpenAiApiTagResponse.class).block();
    }

    public OpenAiApiClassifyResponse checkAds(String text, int type) {
        return openAiApiWebClient
                .mutate()
                .build()
                .post()
                .uri("/adclassify")
                .bodyValue(OpenAiApiTextSummaryRequest.of(text, type))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(OpenAiApiClassifyResponse.class).block();
    }

    /**
     * Papago 번역 API 호출
     */
    public PapagoTranslationResponse translateText(MultiValueMap<String, String> map) {
        return papagoApiWebClient.post()
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
}