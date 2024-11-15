package com.example.currencyexchange.model

import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest
class AccountTest extends Specification {

    @Shared
    Validator validator

    void setupSpec() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
        validator = factory.getValidator()
    }

    @Unroll
    def "test validation for #description"() {
        given:
        def account = new Account(firstName: firstName, lastName: lastName, balancePLN: balancePLN, balanceUSD: balanceUSD)

        when:
        def violations = validator.validate(account)

        then:
        violations.size() == expectedViolationCount

        where:
        description                              | firstName  | lastName   | balancePLN         | balanceUSD    || expectedViolationCount
        "valid account"                          | "Jan"      | "Gaweł"    | new BigDecimal("1000") | new BigDecimal("100") || 0
        "invalid first name with symbol"         | "J@an"     | "Gaweł"    | new BigDecimal("1000") | new BigDecimal("100") || 1
        "invalid last name with symbol"          | "Jan"      | "G@weł"    | new BigDecimal("1000") | new BigDecimal("100") || 1
        "null PLN balance"                       | "Jan"      | "Gaweł"    | null                   | new BigDecimal("100") || 1
        "negative USD balance"                   | "Jan"      | "Gaweł"    | new BigDecimal("1000") | new BigDecimal("-100") || 1
        "empty first name"                       | ""         | "Gaweł"    | new BigDecimal("1000") | new BigDecimal("100") || 2
        "empty last name"                        | "Jan"      | ""         | new BigDecimal("1000") | new BigDecimal("100") || 2
    }
}
