package com.example.currencyexchange.controller;

import com.example.currencyexchange.model.AccountResponse;
import com.example.currencyexchange.model.CurrencyExchangeDTO;
import com.example.currencyexchange.model.Rate;
import com.example.currencyexchange.service.CurrencyService;
import com.example.currencyexchange.service.NbpIntegrationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/currency")
@Tag(name = "Currency Exchange currency API", description = "currency-exchange currency operations")
public class CurrencyExchangeController {
    private final CurrencyService currencyService;
    private final NbpIntegrationService nbpIntegrationService;

    @ApiPost(value = "/exchange")
    public ResponseEntity<AccountResponse> exchangeCurrency(@Valid @RequestBody CurrencyExchangeDTO request) {
        var response = currencyService.exchangeCurrency(request.getId(),
                request.getFromCurrency(),
                request.getAmount());
        return ResponseEntity.ok(response);
    }

    @ApiGet(value = "/rates")
    public ResponseEntity<Rate> getRates() {
        var response = nbpIntegrationService.getExchangeRate();
        return ResponseEntity.ok(response);
    }

    @ApiPost(value = "/add")
    public ResponseEntity<AccountResponse> addCurrency(@Valid @RequestBody CurrencyExchangeDTO request) {
        var response = currencyService.addCurrency(request.getId(),
                request.getFromCurrency(),
                request.getAmount());
        return ResponseEntity.ok(response);
    }

    @ApiPost(value = "/subtract")
    public ResponseEntity<AccountResponse> subtractCurrency(@Valid @RequestBody CurrencyExchangeDTO request) {
        var response = currencyService.subtractCurrency(request.getId(),
                request.getFromCurrency(),
                request.getAmount());
        return ResponseEntity.ok(response);
    }

}
