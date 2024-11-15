package com.example.currencyexchange.controller;

import com.example.currencyexchange.model.Account;
import com.example.currencyexchange.model.AccountResponse;
import com.example.currencyexchange.model.CreateAccountDTO;
import com.example.currencyexchange.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/account")
@Tag(name = "Currency Exchange account API", description = "currency exchange account operations")
public class AccountController {

    private final AccountService accountService;


    @ApiPost(value = "/create")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountDTO request) {
        Account account = accountService.createAccount(request.getFirstName(),
                request.getLastName(),
                request.getInitialPlnBalance());
        var response = new AccountResponse("Successfully created account", account);
        return ResponseEntity.ok(response);
    }

    @ApiGet(value = "/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccount(id);
        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ApiDelete("/delete/{id}")
    public ResponseEntity<AccountResponse> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        var response = new AccountResponse("Successfully deleted account", null);
        return ResponseEntity.ok(response);
    }
}
