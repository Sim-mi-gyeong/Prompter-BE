package com.prompter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prompter.common.CustomException;
import com.prompter.common.Result;
import com.prompter.domain.Site;
import com.prompter.external.gpt.ExternalRestful;
import com.prompter.external.gpt.dto.response.gpt.OpenAiApiSummaryResponse;
import com.prompter.external.gpt.dto.response.gpt.OpenAiApiTagResponse;
import com.prompter.repository.SiteRepository;
import com.prompter.controller.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import org.json.JSONException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class TextService {

    private final SiteRepository siteRepository;
    private final ExternalRestful externalRestful;

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

    public ResultResponse getSummaryAndAnalyzedText(String url, int type) throws JSONException {

        String summary = getSummaryResponse(url, type);
        String[] tags = getTagResponse(url, type).getTag().split(",");
        boolean classifyAdsYn = classifyAdsYn(url, type);

        return ResultResponse.of(
                summary, Arrays.asList(tags), null, classifyAdsYn
            );
    }

    /**
     * 텍스트 관련 API 호출
     */
    @Async("sampleExecutor")
    public String getSummaryResponse(String text, int type) {
        return externalRestful.getTextSummary(text, type);
    }

    @Async("sampleExecutor")
    public Flux<OpenAiApiSummaryResponse> getSummaryResponseByStream(String text, int type) throws JsonProcessingException {
        return externalRestful.getTextSummaryByStream(text, type);
    }

    @Async("sampleExecutor")
    public OpenAiApiTagResponse getTagResponse(String text, int type) {
        return externalRestful.getTags(text, type);
    }

    // 광고 분류 API 호출
    @Async("sampleExecutor")
    private boolean checkAdsByOpenAiApi(String content, int type) {
        return Objects.equals(Objects.requireNonNull(externalRestful.checkAds(content, type).getAd()), "O");
    }

    @Async("sampleExecutor")
    public boolean classifyAdsYn(String content, int type) {
        boolean checkAdsYn = false;
        checkAdsYn = checkAdsByOpenAiApi(content, type);
        checkAdsYn = checkAds(content);
        return checkAdsYn;
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

    /**
     * method
     */
    public String getContent(String url) {
        return findSiteByUrl(url).getContent();
    }

    private Site findSiteByUrl(String url) {
        return siteRepository.findByUrl(url).orElseThrow(() -> new CustomException(Result.NOT_EXIST_URL_SITE));
    }

    private boolean classifyVideo(String url) {
        return url.contains("www.youtube.com");
    }

}
