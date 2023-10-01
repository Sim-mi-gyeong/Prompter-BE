package com.prompter.config;


import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class CustomDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        return () -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            runnable.run();
        };
    }
}
