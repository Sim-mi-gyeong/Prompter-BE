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
import kr.co.shineware.nlp.komoran.model.Token;
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
        List<ResultResponse.Word> words = analyze(site.getContent());

        return ResultResponse.builder()
                .summaryContent(response.getSummary())
                .tags(response.getTag())
                .words(words)
                .build();
    }

    public List<ResultResponse.Word> analyze(String content) {
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

        KomoranResult analyzeResultList = komoran.analyze(content);
        System.out.println(analyzeResultList.getPlainText());

        List<Token> tokenList = analyzeResultList.getTokenList();
        for(Token token : tokenList) {
            System.out.format("(%2d, %2d) %s/%s\n", token.getBeginIndex(),
                    token.getEndIndex(), token.getMorph(), token.getPos());
        }

        List<String> nounList = analyzeResultList.getNouns();
        for(String noun : nounList) {
            System.out.println(noun);
        }

        // 여기서 getMorphesByTags 사용하면 내가원하는 형태소만 뽑아낼 수 있음
        List<String> analyzeList = analyzeResultList.getMorphesByTags("NNP", "NNG", "NNB", "NP");

        HashMap<String, Integer> listHash = new HashMap<>();
        for (String word : analyzeList) {
            int num = Collections.frequency(analyzeList, word);
            log.info("word : {} , num : {}", word, num);
            listHash.put(word, num);
        }

        sortByWordNum(listHash);
        List<ResultResponse.Word> wordList = new ArrayList<>();

        Iterator it = listHash.entrySet().iterator();
        int cnt = 0;
        while (it.hasNext()) {
            cnt += 1;
            Map.Entry<String, Integer> entry = (Map.Entry)it.next();
//            System.out.println(entry.getKey() + " = " + entry.getValue());
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

    private void sortByWordNum(HashMap<String, Integer> map) {
        List<Map.Entry<String, Integer>> entryList = new LinkedList<>(map.entrySet());
        entryList.sort(Map.Entry.comparingByValue());
        List<String> listKeySet = new ArrayList<>(map.keySet());
        // 내림차순 정렬
        Collections.sort(listKeySet, (value1, value2) -> (map.get(value2).compareTo(map.get(value1))));
//        for(String key : listKeySet) {
//            System.out.println("key : " + key + " , " + "value : " + map.get(key));
//        }
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
