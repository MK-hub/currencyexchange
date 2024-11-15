package com.example.currencyexchange.service

import com.example.currencyexchange.model.Account
import com.example.currencyexchange.model.CurrencyType
import com.example.currencyexchange.model.Rate
import com.example.currencyexchange.repository.AccountRepository
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest
class CurrencyServiceTest extends Specification {

    @Autowired
    CurrencyService currencyService

    @SpringBean
    AccountRepository accountRepository = Mock()

    @SpringBean
    NbpIntegrationService nbpIntegrationService = Mock()

    @Unroll
    def "test successful currency exchange from #amount #fromCurrency with account balance of #initialBalanceUSD USD and #initialBalancePLN PLN"() {
        given: "an account with initial balances"
        def account = new Account(id: 1L, balanceUSD: initialBalanceUSD, balancePLN: initialBalancePLN)
        accountRepository.findById(1L) >> Optional.of(account)
        nbpIntegrationService.getExchangeRate() >> new Rate(new BigDecimal("4.2"), new BigDecimal("4.0"))

        when: "currency is exchanged"
        def response = currencyService.exchangeCurrency(1L, fromCurrency, amount)

        then: "a success message is returned"
        response.message == message

        where:
        fromCurrency     | amount                 | initialBalanceUSD      | initialBalancePLN      || message
        CurrencyType.USD | new BigDecimal("100")  | new BigDecimal("1000") | new BigDecimal("4000") || "Successfully exchanged 100 USD to 420.0 PLN"
        CurrencyType.PLN | new BigDecimal("400")  | new BigDecimal("1000") | new BigDecimal("4000") || "Successfully exchanged 400 PLN to 100.00 USD"
    }

    @Unroll
    def "test failed currency exchange from #amount #fromCurrency with account balance of #initialBalanceUSD USD and #initialBalancePLN PLN"() {
        given: "an account with initial balances"
        def account = new Account(id: 1L, balanceUSD: initialBalanceUSD, balancePLN: initialBalancePLN)
        accountRepository.findById(1L) >> Optional.of(account)
        nbpIntegrationService.getExchangeRate() >> new Rate(new BigDecimal("4.2"), new BigDecimal("4.0"))

        when: "currency is exchanged"
        currencyService.exchangeCurrency(1L, fromCurrency, amount)

        then: "a validation error is thrown"
        thrown(expectedException)

        where:
        fromCurrency     | amount                 | initialBalanceUSD      | initialBalancePLN      || expectedException
        CurrencyType.PLN | new BigDecimal("5000") | new BigDecimal("1000") | new BigDecimal("4000") || IllegalArgumentException
        CurrencyType.USD | new BigDecimal("1100") | new BigDecimal("1000") | new BigDecimal("4000") || IllegalArgumentException
    }

    @Unroll
    def "should successfully add #amount #currency to account with initial balance USD=#initialBalanceUSD PLN=#initialBalancePLN"() {
        given: "an account with initial balances"
        def account = new Account(id: 1L, balanceUSD: initialBalanceUSD, balancePLN: initialBalancePLN)
        accountRepository.findById(1L) >> Optional.of(account)

        when: "currency is added"
        def response = currencyService.addCurrency(1L, currency, amount)

        then: "the balance is updated correctly"
        response.message == "Successfully added currency"
        response.account.balanceUSD == expectedBalanceUSD
        response.account.balancePLN == expectedBalancePLN


        where:
        currency         | amount                | initialBalanceUSD      | initialBalancePLN      || expectedBalanceUSD      | expectedBalancePLN
        CurrencyType.USD | new BigDecimal("100") | new BigDecimal("1000") | new BigDecimal("4000") || new BigDecimal("1100") | new BigDecimal("4000")
        CurrencyType.PLN | new BigDecimal("500") | new BigDecimal("1000") | new BigDecimal("4000") || new BigDecimal("1000") | new BigDecimal("4500")
    }


    @Unroll
    def "should successfully subtract #amount #currency from account with initial balance USD=#initialBalanceUSD PLN=#initialBalancePLN"() {
        given: "an account with initial balances"
        def account = new Account(id: 1L, balanceUSD: initialBalanceUSD, balancePLN: initialBalancePLN)
        accountRepository.findById(1L) >> Optional.of(account)

        when: "currency is subtracted"
        def response = currencyService.subtractCurrency(1L, currency, amount)

        then: "the balance is updated correctly"
        response.message == "Successfully subtracted currency"
        response.account.balanceUSD == expectedBalanceUSD
        response.account.balancePLN == expectedBalancePLN

        where:
        currency         | amount                | initialBalanceUSD      | initialBalancePLN      || expectedBalanceUSD      | expectedBalancePLN
        CurrencyType.USD | new BigDecimal("100") | new BigDecimal("1000") | new BigDecimal("4000") || new BigDecimal("900")  | new BigDecimal("4000")
        CurrencyType.PLN | new BigDecimal("500") | new BigDecimal("1000") | new BigDecimal("4000") || new BigDecimal("1000") | new BigDecimal("3500")
    }

    @Unroll
    def "should throw exception when subtracting #amount #currency with insufficient funds"() {
        given: "an account with initial balances"
        def account = new Account(id: 1L, balanceUSD: initialBalanceUSD, balancePLN: initialBalancePLN)
        accountRepository.findById(1L) >> Optional.of(account)

        when: "attempting to subtract more than available"
        currencyService.subtractCurrency(1L, currency, amount)

        then: "an exception is thrown"
        thrown(IllegalArgumentException)

        where:
        currency         | amount                 | initialBalanceUSD      | initialBalancePLN
        CurrencyType.USD | new BigDecimal("1100") | new BigDecimal("1000") | new BigDecimal("4000")
        CurrencyType.PLN | new BigDecimal("4500") | new BigDecimal("1000") | new BigDecimal("4000")
    }
}
