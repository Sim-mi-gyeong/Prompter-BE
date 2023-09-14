package com.prompter.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prompter.common.CustomException;
import com.prompter.domain.Site;
import com.prompter.dto.response.CrawlingResponse;
import com.prompter.dto.response.ResultResponse;
import com.prompter.dto.response.SummaryResponse;
import com.prompter.external.ExternalRestful;
import com.prompter.external.dto.response.OpenAiApiSummaryResponse;
import com.prompter.external.dto.response.OpenAiApiTagResponse;
import com.prompter.repository.SiteRepository;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.json.JSONException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Stream;

import static com.prompter.common.Result.NOT_EXIST_URL_SITE;

@Service
@Slf4j
@RequiredArgsConstructor
public class TextService {

    private final SiteRepository siteRepository;
    private final ExternalRestful externalRestful;

    public CrawlingResponse getTextContent(String url) throws JSONException {
        return CrawlingResponse.from(findSiteByUrl(url).getContent());
    }

    public SummaryResponse getSummaryText(String url) throws JSONException {

        Site site = findSiteByUrl(url);
        OpenAiApiSummaryResponse response = getSummaryResponse(site.getContent());

        return SummaryResponse.of(response.getSummary(), Arrays.asList(getTagResponse(site.getContent()).getTag().split(",")));
    }

    public SummaryResponse getSummaryTextByStream(String url) throws JSONException, JsonProcessingException {

        Site site = findSiteByUrl(url);
        Flux<OpenAiApiSummaryResponse> response = getSummaryResponseByStream(site.getContent());

        return SummaryResponse.of(Objects.requireNonNull(response.blockFirst()).getSummary(), null);
    }

    public Flux<SummaryResponse> getSummaryTextByStream2(String url) throws JSONException, JsonProcessingException {

        Site site = findSiteByUrl(url);

        return externalRestful.getTextSummaryByStream2(site.getContent())
            .flatMap(summaryResponse -> {
                if (ObjectUtils.isEmpty(summaryResponse.getSummary())) {
                    log.info("text : {}", summaryResponse.getSummary());
                    // return Flux.just(SummaryResponse.of("", null));
                } else {
                    log.info("text : {}", summaryResponse.getSummary());
                }
                return Flux.just(SummaryResponse.of(summaryResponse.getSummary(), null));
            });

    }

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

    public ResultResponse getSummaryAndAnalyzedText(String url) throws JSONException {

        Site site = findSiteByUrl(url);

        OpenAiApiSummaryResponse response = getSummaryResponse(site.getContent());
        // List<ResultResponse.Word> words = analyze(site.getContent());
        boolean classifyAdsYn = classifyAdsYn(site.getContent());

        return ResultResponse.of(response.getSummary(), Arrays.asList(getTagResponse(site.getContent()).getTag().split(",")), null, classifyAdsYn);
    }

    @Async("sampleExecutor")
    public boolean classifyAdsYn(String content) {
        boolean checkAdsYn = false;
        checkAdsYn = checkAdsByOpenAiApi(content);
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

    // 광고 분류 API 호출
//    @Async
    private boolean checkAdsByOpenAiApi(String content) {
        return externalRestful.checkAds(content).getAd().equals("O");
    }

    @Async("sampleExecutor")
    public List<ResultResponse.Word> analyze(String content) {
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

        KomoranResult analyzeResultList = komoran.analyze(content);

        /**
         * getMorphesByTags() : 특정 형태소 추출 가능
         * NN : 명사 , MAG : 일반 부사 , PA : 형용사 , PV : 동사
         * NNB : 일반 의존 명사 , NNG : 보통명사 , NNM : 단위 의존 명사 , NNP : 고유 명사 , NP : 대명사
         */
        HashMap<String, Integer> nounMap = new HashMap<>();
        List<String> nounList = analyzeResultList.getMorphesByTags("NNP", "NNG");
        Stream<String> streamNounList = nounList.stream();
        streamNounList
                .forEach(
                       word -> {
                           int num = Collections.frequency(nounList, word);
//                           log.info("word : {} , num : {}", word, num);
                           nounMap.put(word, num);
                       }
                );

        List<Map.Entry<String, Integer>> entryList = sortByWordNum(nounMap);
        
        List<ResultResponse.Word> wordList = new ArrayList<>();
        Iterator it = entryList.iterator();
        int cnt = 0;
        while (it.hasNext()) {
            cnt += 1;
            Map.Entry<String, Integer> entry = (Map.Entry)it.next();
            wordList.add(
                    ResultResponse.Word.builder()
                            .text(entry.getKey())
                            .number(entry.getValue())
                            .build()
            );
            if (cnt == 20) {
                break;
            }
        }
        return wordList;
    }

    private List<Map.Entry<String, Integer>> sortByWordNum(HashMap<String, Integer> map) {
        List<Map.Entry<String, Integer>> entryList = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return entryList;
    }

    // OpenAi API 호출
    @Async("sampleExecutor")
    public OpenAiApiSummaryResponse getSummaryResponse(String text) {
        return externalRestful.getTextSummary(text);
    }

    @Async("sampleExecutor")
    public Flux<OpenAiApiSummaryResponse> getSummaryResponseByStream(String text) throws JsonProcessingException {
        return externalRestful.getTextSummaryByStream(text);
    }

    @Async("sampleExecutor")
    public Flux<OpenAiApiSummaryResponse> getSummaryResponseByStream2(String text) throws JsonProcessingException {
        return externalRestful.getTextSummaryByStream2(text);
    }

    @Async("sampleExecutor")
    public OpenAiApiTagResponse getTagResponse(String text) {
        return externalRestful.getTags(text);
    }

    public String getContent(String url) {
        return findSiteByUrl(url).getContent();
    }

    private Site findSiteByUrl(String url) {
        return siteRepository.findByUrl(url).orElseThrow(() -> new CustomException(NOT_EXIST_URL_SITE));
    }
}
