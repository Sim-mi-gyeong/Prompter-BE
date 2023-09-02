package com.prompter.controller;

import com.prompter.common.CustomException;
import com.prompter.domain.Site;
import com.prompter.dto.response.CrawlingResponse;
import com.prompter.dto.response.ResultResponse;
import com.prompter.dto.response.SummaryResponse;
import com.prompter.external.ExternalRestful;
import com.prompter.external.OpenAiApiSummaryResponse;
import com.prompter.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.stereotype.Service;


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
        log.info("url : {}", url);
        log.info("content : {}", site.getContent());
        OpenAiApiSummaryResponse response = getSummaryResponse(site.getContent());

        log.info("response.getText() : {}", response.getSummary());
        log.info("response.getTag() : {}", response.getTag());

        String strTags = response.getTag();
        boolean enableTagParsing = false;
        String[] arrTags = new String[0];
        if (strTags.contains(",")) {
            enableTagParsing = true;
            arrTags = strTags.split(",");
        }

//        return SummaryResponse.of(response.getText(), enableTagParsing ? arrTags : response.getTag().split("."));
        return SummaryResponse.of(response.getSummary(), response.getTag());
    }

    public ResultResponse getSummaryAndAnalyzedText(String url) throws JSONException {

        Site site = findSiteByUrl(url);

        OpenAiApiSummaryResponse response = getSummaryResponse(site.getContent());


        return null;
    }

      // 해당 url 에 맞는 크롤링 결과 리턴

    // OpenAi API 호출
    public OpenAiApiSummaryResponse getSummaryResponse(String text) {
        return externalRestful.getTextSummary(text);
    }

    public String getContent(String url) {
        return findSiteByUrl(url).getContent();
    }

    private Site findSiteByUrl(String url) {
        return siteRepository.findByUrl(url).orElseThrow(() -> new CustomException(NOT_EXIST_URL_SITE));
    }
}
