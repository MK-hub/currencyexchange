package com.example.currencyexchange.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountDTO {

    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    @NotNull(message = "Initial balance cannot be null")
    @Positive(message = "Initial balance must be positive")
    private BigDecimal initialPlnBalance;
}
