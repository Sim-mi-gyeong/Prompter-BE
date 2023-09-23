package com.prompter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prompter.external.gpt.ExternalClientProperties;
import com.prompter.external.gpt.ExternalRestful;
import com.prompter.external.gpt.dto.response.gpt.OpenAiApiResultResponse;
import com.prompter.controller.response.ResultResponse;
import com.prompter.external.gpt.dto.response.search.SearchDictionaryResponse;
import com.prompter.external.gpt.dto.response.wikipedia.wikipediaApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;

import org.json.JSONException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TextService {

    private final ExternalRestful externalRestful;
    private final ExternalClientProperties externalClientProperties;

    private static final Double PERCENT_GPT = 39.5;
    private static final Double PERCENT_RULE_BASE = 59.5;

//    public Flux<SummaryResponse> getSummaryTextByStream2(String url) throws JSONException, JsonProcessingException {
//
//        Site site = findSiteByUrl(url);
//
//        return externalRestful.getTextSummaryByStream2(site.getContent())
//            .flatMap(summaryResponse -> {
//                if (ObjectUtils.isEmpty(summaryResponse.getSummary())) {
//                    log.info("text : {}", summaryResponse.getSummary());
//                    // return Flux.just(SummaryResponse.of("", null));
//                } else {
//                    log.info("text : {}", summaryResponse.getSummary());
//                }
//                return Flux.just(SummaryResponse.of(summaryResponse.getSummary(), null));
//            });
//
//    }


    /*
    @ContentsControl(access = AgeStatusType.CHILD)
	@Override
	public Mono<AuthenticationEmailResponse> updateParentEmail(String ppsn, ParentEmailRequest request, ImsProfileInfo imsProfileInfo, Country country) {
		var siisResponseSequence = siisRestful.create(new SBooksCreateRequest(ppsn, request.getParentEmail(), EXPIRED_SECONDS));

		var nTicketResponseSequence = profileCoreRestful.getProfileByPpsn(ppsn)
			.flatMap(profileResponse -> htmlReaderService.readEmailBody(emailProperty.getFileName(request.getType(), country))
				.flatMap(contents -> nTicketRestful.send(new NTicketSendEmailRequest(
					emailProperty.getSubject(request.getType(), country),
					request.getParentEmail(),
					ppsn,
					contents,
					profileResponse.getData().getProfileName())
				)));


		return Mono.zip(siisResponseSequence, nTicketResponseSequence, (siis, nTicket) -> {
			long expiredAt = Instant.now().toEpochMilli() + (CACHE_EXPIRED_SECONDS * 1000);

			profileCacheRepository.save(String.format(CONTENTS_CONTROL_EMAIL_HASH_KEY, ppsn),
					new ContentsControlVerification(siis.getHashKey(), nTicket.getAuthenticationCode()), CACHE_EXPIRED_SECONDS)
				.subscribe();

			return new AuthenticationEmailResponse(expiredAt);
		});
	}
     */

/*
    @Override
	public Mono<ContentsControlResponse> get(String ppsn, ImsProfileInfo imsProfileInfo, Country country, String requestedFrom) {
		return profileCoreRestful.getContentsControl(ppsn)
			.flatMap(contentsControlResponse -> {
				if (contentsControlResponse.isUseParentEmail()) {
					return siisRestful.get(contentsControlResponse.getEmailHashKey())
						.flatMap(siisResponse ->
							Mono.just(ContentsControlResponse.of(contentsControlResponse, siisResponse.getContent(), imsProfileInfo, requestedFrom)));
				}

				return Mono.just(ContentsControlResponse.of(contentsControlResponse, imsProfileInfo));
			})
			.onErrorResume(
				ProfileException.class,
				throwable -> {
					if (Result.NOT_FOUND_CONTENTS_CONTROL.equals(throwable.getResult())) {
						return profileCoreRestful.createContentsControl(
							new ProfileCoreContentsControlCreateRequest(ppsn,
								ContentsControlDefaultType.of(imsProfileInfo.determineAgeStatus(), country.getCountryCodeOrDefaultCountryCode())))
							.flatMap(createResponse -> Mono.just(ContentsControlResponse.of(createResponse, imsProfileInfo)));
					}

					return Mono.error(new ProfileException(Result.FAIL));
				}
			);
	}
 */

    public ResultResponse getSummaryAndAnalyzedText(String url, int type, String language) throws JSONException {

        OpenAiApiResultResponse clientResponse = getSummaryResponse(url, type);

        String[] tags = clientResponse.getTags().split(",");

        List<ResultResponse.Keyword> keywords = Arrays.stream(tags)
                .map(this::createKeyword).collect(Collectors.toList());

        log.info("keywords.size() : {}", keywords.size());

        // Rule Base 광고 분류 적용
        boolean classifyAdsYn = clientResponse.getAdYn().equals("O");
        return ResultResponse.of(clientResponse.getTitle(),
                clientResponse.getSummary(), Arrays.asList(tags), keywords,
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

    @Async("sampleExecutor")
    public Flux<OpenAiApiResultResponse> getSummaryResponseByStream(String text, int type) throws JsonProcessingException {
        return externalRestful.getTextSummaryByStream(text, type);
    }

    /**
     * 네이버 검색 - 백과사전
     */
    public ResultResponse.Keyword createKeyword(String tag) {
        SearchDictionaryResponse searchDictionaryResponse = searchByDictionary(tag);
        return  new ResultResponse.Keyword(
                tag.replace(" ", ""),
                getDescription(searchDictionaryResponse),
                getLink(searchDictionaryResponse)
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
}
