package org.j2os.api;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestI18nController {

    private final MessageSource messageSource;

    @GetMapping("/i18n")
    public String testI18n() {
        return messageSource.getMessage(
                "account.owner.name.size",
                null,
                LocaleContextHolder.getLocale()
        );
    }
}
