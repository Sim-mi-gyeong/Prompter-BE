package com.prompter.controller;


import com.prompter.common.CustomResponseEntity;
import com.prompter.controller.request.TranslationRequest;
import com.prompter.controller.response.TranslationResponse;
import com.prompter.service.TranslationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/translate")
@RestController
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping
    public CustomResponseEntity<TranslationResponse> translateText(
            @RequestBody @Valid TranslationRequest request) {
        return CustomResponseEntity.success(translationService.translateText(request));
    }
}
