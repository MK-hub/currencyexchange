package com.example.currencyexchange.service;

import com.example.currencyexchange.errorhandling.AccountAlreadyExistsException;
import com.example.currencyexchange.errorhandling.AccountNotFoundException;
import com.example.currencyexchange.logging.TrackTime;
import com.example.currencyexchange.model.Account;
import com.example.currencyexchange.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.currencyexchange.config.cache.RedisConfig.ACCOUNT_CACHE;
import static com.example.currencyexchange.config.cache.RedisConfig.CUSTOM_GENERATOR;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository repository;


    @TrackTime
    @Transactional(rollbackFor = Exception.class)
    public Account createAccount(String firstName, String lastName, BigDecimal initialBalancePLN) {
        if (repository.existsByFirstNameAndLastName(firstName, lastName)) {
            throw new AccountAlreadyExistsException("Account with given first name and last name already exists");
        }
        var account = new Account();
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setBalancePLN(initialBalancePLN);
        account.setBalanceUSD(BigDecimal.ZERO);
        return repository.save(account);
    }

    @TrackTime
    @Cacheable( value = ACCOUNT_CACHE, keyGenerator = CUSTOM_GENERATOR)
    public Optional<Account> getAccount(Long id) {
        if (!repository.existsById(id)) {
            throw new AccountNotFoundException("Account with ID " + id + " not found");
        }
        return repository.findById(id);
    }


    @TrackTime
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(Long accountId) {
        if (!repository.existsById(accountId)) {
            throw new AccountNotFoundException("Account with ID " + accountId + " not found");
        }
        repository.deleteById(accountId);
    }

}
