package com.example.currencyexchange.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CurrencyExchangeDTO {

    @NotNull(message = "Id cannot be null")
    private Long id;

    @NotNull(message = "From currency cannot be null")
    private CurrencyType fromCurrency;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

}
