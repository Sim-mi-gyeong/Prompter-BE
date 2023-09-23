package com.prompter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prompter.common.LanguageCode;
import com.prompter.external.gpt.ExternalClientProperties;
import com.prompter.external.gpt.ExternalRestful;
import com.prompter.external.gpt.dto.response.gpt.OpenAiApiResultResponse;
import com.prompter.controller.response.ResultResponse;
import com.prompter.external.gpt.dto.response.wikipedia.wikipediaApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import org.json.JSONException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class TextService {

    private final ExternalRestful externalRestful;
    private final ExternalClientProperties externalClientProperties;

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
        
        List<Optional<ResultResponse.Keyword>> keywords = Arrays.stream(tags)
                .map(tag -> getWikipediaContent(tag, language).getQuery().getPages().values()
                        .stream()
                        .map(pageData -> new ResultResponse.Keyword(
                                        tag.replace(" ", ""),
                                        pageData.getExtract(),
                                        language.equals(LanguageCode.EN.getDesc()) ? externalClientProperties.getWikipediaApi().getEnPageUrl() + tag.replace(" ", "") : externalClientProperties.getWikipediaApi().getKoPageUrl() + tag.replace(" ", "")
                                )
                        ).findFirst()
                ).collect(Collectors.toList());

        log.info("keywords.size() : {}", keywords.size());

        // Rule Base 광고 분류 적용
        boolean classifyAdsYn = classifyAdsYn(url, clientResponse.getAdYn().equals("O"));

        return ResultResponse.of(clientResponse.getTitle(),
                clientResponse.getSummary(), Arrays.asList(tags), keywords, 0
            );
    }

    @Async("sampleExecutor")
    public OpenAiApiResultResponse getSummaryResponse(String url, int type) {
        return externalRestful.getSummary(url, type);
    }

    @Async("sampleExecutor")
    public Flux<OpenAiApiResultResponse> getSummaryResponseByStream(String text, int type) throws JsonProcessingException {
        return externalRestful.getTextSummaryByStream(text, type);
    }

    @Async("sampleExecutor")
    public boolean classifyAdsYn(String content, boolean checkAdsByOpenAiApi) {
        // Rule Base
        boolean checkAdsYn = checkAds(content);

        if (!checkAdsByOpenAiApi && checkAdsYn) {
            return true;
        } else {
            return false;
        }
//        return checkAdsYn;
    }

    /**
     * 위키피디아
     */
    public wikipediaApiResponse getWikipediaContent(String keyword, String language) {
        return externalRestful.getWikipediaContent(keyword, language);
    }

    /**
     * 5회 이상 포함 시 광고로 분류
     * 소정의 / 원고료 / 업체로부터 / 업체로
     */
    private boolean checkAds(String content) {
        int cnt = 0;
        if (content.contains("소정의") || content.contains("원고료") || content.contains("업체로") || content.contains("업체로부터")) {
            return true;
        }
        return false;
    }
}
