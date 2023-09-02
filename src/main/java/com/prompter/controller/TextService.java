package com.prompter.controller;

import com.prompter.common.CustomException;
import com.prompter.domain.Site;
import com.prompter.dto.response.CrawlingResponse;
import com.prompter.dto.response.ResultResponse;
import com.prompter.dto.response.SummaryResponse;
import com.prompter.external.ExternalRestful;
import com.prompter.external.OpenAiApiSummaryResponse;
import com.prompter.repository.SiteRepository;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.stereotype.Service;


import java.util.*;

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

        return SummaryResponse.of(response.getSummary(), Arrays.asList(response.getTag().split(",")));
    }

    public ResultResponse getSummaryAndAnalyzedText(String url) throws JSONException {

        Site site = findSiteByUrl(url);

        OpenAiApiSummaryResponse response = getSummaryResponse(site.getContent());
        List<ResultResponse.Word> words = analyze(site.getContent());

        return ResultResponse.of(response.getSummary(), Arrays.asList(response.getTag().split(",")), words, false);
    }

    private List<ResultResponse.Word> analyze(String content) {
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

        KomoranResult analyzeResultList = komoran.analyze(content);

        /**
         * getMorphesByTags() : 특정 형태소 추출 가능
         * NN : 명사 , MAG : 일반 부사 , PA : 형용사 , PV : 동사
         * NNB : 일반 의존 명사 , NNG : 보통명사 , NNM : 단위 의존 명사 , NNP : 고유 명사 , NP : 대명사
         */
        HashMap<String, Integer> nounMap = new HashMap<>();
        List<String> nounList = analyzeResultList.getMorphesByTags("NNP", "NNG");
        for (String word : nounList) {
            int num = Collections.frequency(nounList, word);
            log.info("word : {} , num : {}", word, num);
            nounMap.put(word, num);
        }

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
