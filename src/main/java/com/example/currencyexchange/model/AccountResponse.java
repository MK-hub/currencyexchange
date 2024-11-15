package com.example.currencyexchange.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountResponse {
    private String message;
    private Account account;
}
