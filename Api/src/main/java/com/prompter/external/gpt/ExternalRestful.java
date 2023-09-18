package com.prompter.external.gpt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prompter.external.gpt.dto.request.OpenAiApiSummaryRequest;
import com.prompter.external.gpt.dto.response.OpenAiApiClassifyResponse;
import com.prompter.external.gpt.dto.response.OpenAiApiSummaryResponse;
import com.prompter.external.gpt.dto.response.OpenAiApiTagResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalRestful {

    private final WebClient openAiApiWebClient;

    public String getTextSummary(String text) {
        return openAiApiWebClient
                .mutate()
                .build()
                .post()
                .uri("/summary")
                .bodyValue(OpenAiApiSummaryRequest.from(text))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class).block();
    }

    public Flux<OpenAiApiSummaryResponse> getTextSummaryByStream(String text) throws JsonProcessingException {
        Flux<OpenAiApiSummaryResponse> streamText = openAiApiWebClient
                .mutate()
                .build()
                .post()
                .uri("/summary")
                .bodyValue(OpenAiApiSummaryRequest.from(text))
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

    public OpenAiApiTagResponse getTags(String text) {
        return openAiApiWebClient
                .mutate()
                .build()
                .post()
                .uri("/tagging")
                .bodyValue(OpenAiApiSummaryRequest.from(text))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(OpenAiApiTagResponse.class).block();
    }

    public OpenAiApiClassifyResponse checkAds(String text) {
        return openAiApiWebClient
                .mutate()
                .build()
                .post()
                .uri("/adclassify")
                .bodyValue(OpenAiApiSummaryRequest.from(text))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(OpenAiApiClassifyResponse.class).block();
    }
}