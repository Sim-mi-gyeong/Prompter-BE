package com.prompter.service;

import com.prompter.controller.request.TranslationRequest;
import com.prompter.controller.response.TranslationResponse;
import com.prompter.external.gpt.ExternalRestful;
import com.prompter.external.gpt.dto.response.papago.PapagoTranslationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final ExternalRestful externalRestful;

    public TranslationResponse translateText(TranslationRequest request) {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("source", request.getSrcLanguage());
        map.add("target", request.getDstLanguage());
        map.add("text", request.getText());

        PapagoTranslationResponse clientResponse = externalRestful.translateText(map);
        return TranslationResponse.of(
                clientResponse.getMessage().getResult().getSrcLangType()
                , clientResponse.getMessage().getResult().getTarLangType()
                , clientResponse.getMessage().getResult().getTranslatedText()
        );
    }
}
