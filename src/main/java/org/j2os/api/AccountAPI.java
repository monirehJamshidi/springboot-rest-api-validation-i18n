package org.j2os.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.j2os.entity.Account;
import org.j2os.service.AccountClassicService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountAPI {

    private final AccountClassicService accountClassicService;

    @RequestMapping("/saveAndFindAll.do")
    public List<Account> createAccounts(@Valid @RequestBody Account account){
        log.info("creating Account and get list of Account");
        accountClassicService.save(account);
        return accountClassicService.getAccounts();
    }
}