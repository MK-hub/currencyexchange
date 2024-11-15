package com.example.currencyexchange.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "First name cannot be empty")
    @Pattern(regexp = "[\\p{L}\\p{M}]+", message = "First name must be alphabetic")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    @Pattern(regexp = "[\\p{L}\\p{M}]+", message = "First name must be alphabetic")
    private String lastName;

    @NotNull(message = "PLN Balance cannot be null")
    @Positive(message = "PLN Balance must be positive")
    private BigDecimal balancePLN;

    @NotNull(message = "USD Balance cannot be null")
    @PositiveOrZero(message = "USD Balance cannot be negative")
    private BigDecimal balanceUSD = BigDecimal.ZERO;

}
