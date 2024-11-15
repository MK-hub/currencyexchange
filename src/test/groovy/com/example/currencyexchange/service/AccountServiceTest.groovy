package com.example.currencyexchange.service

import com.example.currencyexchange.errorhandling.AccountAlreadyExistsException
import com.example.currencyexchange.errorhandling.AccountNotFoundException
import com.example.currencyexchange.model.Account
import com.example.currencyexchange.repository.AccountRepository
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static java.util.Optional.of

@SpringBootTest
class AccountServiceTest extends Specification {

    @Autowired
    AccountService accountService

    @SpringBean
    AccountRepository repository = Mock()


    def "should create account if does not exist"() {
        given:
        def firstName = "Jan"
        def lastName = "Gaweł"
        def initialBalancePLN = new BigDecimal("100.00")
        def account = new Account(firstName: firstName, lastName: lastName, balancePLN: initialBalancePLN, balanceUSD: BigDecimal.ZERO)

        when:
        def createdAccount = accountService.createAccount(firstName, lastName, initialBalancePLN)

        then:
        1 * repository.existsByFirstNameAndLastName(firstName, lastName) >> false
        1 * repository.save(_ as Account) >> account
        createdAccount.firstName == firstName
        createdAccount.lastName == lastName
        createdAccount.balancePLN == initialBalancePLN
        createdAccount.balanceUSD == BigDecimal.ZERO
    }

    def "should throw exception if account already exists"() {
        given:
        def firstName = "Jane"
        def lastName = "Doe"
        def initialBalancePLN = new BigDecimal("200.00")

        when:
        accountService.createAccount(firstName, lastName, initialBalancePLN)
        then:
        1 * repository.existsByFirstNameAndLastName(firstName, lastName) >> true
        thrown(AccountAlreadyExistsException)
        0 * repository.save(_ as Account)
    }

    def "should get account by ID if exists"() {
        given:
        def accountId = 1L
        def account = new Account(id: accountId, firstName: "John", lastName: "Doe", balancePLN: new BigDecimal("100.00"), balanceUSD: BigDecimal.ZERO)

        when:
        def retrievedAccount = accountService.getAccount(accountId)

        then:
        1 * repository.existsById(accountId) >> true
        1 * repository.findById(accountId) >> of(account)
        retrievedAccount.get().id == accountId
        retrievedAccount.get().firstName == "John"
        retrievedAccount.get().lastName == "Doe"
    }

    def "should throw exception if account does not exist"() {
        given:
        def accountId = 2L

        when:
        accountService.getAccount(accountId)

        then:
        1 * repository.existsById(accountId) >> false
        thrown(AccountNotFoundException)
        0 * repository.findById(accountId)
    }

    def "should delete account by ID if exists"() {
        given:
        def accountId = 3L

        when:
        accountService.deleteAccount(accountId)
        then:
        1 * repository.existsById(accountId) >> true
        1 * repository.deleteById(accountId)
    }

    def "should throw exception if account to delete does not exist"() {
        given:
        def accountId = 4L

        when:
        accountService.deleteAccount(accountId)

        then:
        1* repository.existsById(accountId) >> false
        thrown(AccountNotFoundException)
        0 * repository.deleteById(accountId)
    }
}
