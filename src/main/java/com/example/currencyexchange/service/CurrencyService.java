package com.example.currencyexchange.service;

import com.example.currencyexchange.logging.TrackTime;
import com.example.currencyexchange.model.Account;
import com.example.currencyexchange.model.AccountResponse;
import com.example.currencyexchange.model.CurrencyType;
import com.example.currencyexchange.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final AccountRepository accountRepository;
    private final NbpIntegrationService nbpIntegrationService;


    @TrackTime
    @Transactional(rollbackFor = Exception.class)
    public AccountResponse exchangeCurrency(Long accountId, CurrencyType fromCurrency, BigDecimal amount) {
        var account = getAccount(accountId);
        String message;
        switch (fromCurrency) {
            case USD -> {
                ensureSufficientFunds(account.getBalanceUSD(), amount);
                var exchangeRate = nbpIntegrationService.getExchangeRate();
                var change = amount.multiply(exchangeRate.getBid());
                account.setBalancePLN(account.getBalancePLN().add(change));
                account.setBalanceUSD(account.getBalanceUSD().subtract(amount));
                message = String.format("Successfully exchanged %s USD to %s PLN", amount, change);
            }
            case PLN -> {
                ensureSufficientFunds(account.getBalancePLN(), amount);
                var exchangeRate = nbpIntegrationService.getExchangeRate();
                var change = amount.divide(exchangeRate.getAsk(), 2, RoundingMode.HALF_UP);
                account.setBalancePLN(account.getBalancePLN().subtract(amount));
                account.setBalanceUSD(account.getBalanceUSD().add(change));
                message = String.format("Successfully exchanged %s PLN to %s USD", amount, change);
            }
            default -> throw new IllegalArgumentException("Unsupported currency conversion");

        }
        return new AccountResponse(message, account);
    }

    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
    }

    private void ensureSufficientFunds(BigDecimal balance, BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }

    @TrackTime
    @Transactional(rollbackFor = Exception.class)
    public AccountResponse addCurrency(Long accountId, CurrencyType currency, BigDecimal amount) {
        var account = getAccount(accountId);
        switch (currency) {
            case USD -> account.setBalanceUSD(account.getBalanceUSD().add(amount));
            case PLN -> account.setBalancePLN(account.getBalancePLN().add(amount));
            default -> throw new IllegalArgumentException("Unsupported currency");
        }
        return new AccountResponse("Successfully added currency", account);
    }

    @TrackTime
    @Transactional(rollbackFor = Exception.class)
    public AccountResponse subtractCurrency(Long accountId, CurrencyType currency, BigDecimal amount) {
        var account = getAccount(accountId);
        switch (currency) {
            case PLN -> {
                ensureSufficientFunds(account.getBalancePLN(), amount);
                account.setBalancePLN(account.getBalancePLN().subtract(amount));
            }
            case USD -> {
                ensureSufficientFunds(account.getBalanceUSD(), amount);
                account.setBalanceUSD(account.getBalanceUSD().subtract(amount));
            }
            default -> throw new IllegalArgumentException("Unsupported currency");
        }
        return new AccountResponse("Successfully subtracted currency", account);
    }
}
