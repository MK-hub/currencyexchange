package com.example.currencyexchange.controller

import com.example.currencyexchange.errorhandling.AccountNotFoundException
import com.example.currencyexchange.errorhandling.ErrorHandler
import com.example.currencyexchange.model.Account
import com.example.currencyexchange.model.AccountResponse
import com.example.currencyexchange.model.CurrencyExchangeDTO
import com.example.currencyexchange.model.CurrencyType
import com.example.currencyexchange.service.CurrencyService
import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
class CurrencyExchangeControllerTest extends Specification {

    static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(100.00).setScale(2)
    static final CurrencyType TEST_CURRENCY = CurrencyType.USD

    @Autowired
    CurrencyExchangeController currencyExchangeController

    @Autowired
    ErrorHandler errorHandler

    @SpringBean
    CurrencyService currencyService = Mock()

    MockMvc mockMvc

    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(currencyExchangeController)
                .setControllerAdvice(errorHandler)
                .build()
    }


    @Unroll
    def "test exchangeCurrency with ID #id"() {
        given:
        def request = createRequest(id)
        def jsonRequest = toJson(request)

        and: "mock service behavior"
        if (accountExists) {
            def account = createAccount(id)
            def response = new AccountResponse("Successfully exchanged currency", account)
            1 * currencyService.exchangeCurrency(id.toLong(), TEST_CURRENCY, TEST_AMOUNT) >> response
        } else {
            1 * currencyService.exchangeCurrency(id.toLong(), TEST_CURRENCY, TEST_AMOUNT) >>
            { throw new AccountNotFoundException("Account with ID " + id + " not found") }
        }

        when:
        def result = mockMvc.perform(post("/api/currency/exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequest))

        then:
        result.andExpect(status().is(expectedStatus))
        if (expectedStatus == 200) {
            result.andExpect(jsonPath('$.message').value("Successfully exchanged currency"))
            result.andExpect(jsonPath('$.account.id').value(id))
            result.andExpect(jsonPath('$.account.firstName').value("name"))
            result.andExpect(jsonPath('$.account.lastName').value("lastName"))
            result.andExpect(jsonPath('$.account.balancePLN').value(100.0))
            result.andExpect(jsonPath('$.account.balanceUSD').value(100.0))
        } else if (expectedStatus == 404) {
            result.andExpect(jsonPath('$.message').value("Account with ID " + id + " not found"))
        }

        where:
        id || accountExists || expectedStatus
        1  || true          || 200
        2  || false         || 404
    }

    @Unroll
    def "test addCurrency with ID #id"() {
        given:
        def request = createRequest(id)
        def jsonRequest = toJson(request)

        if (accountExists) {
            def account = createAccount(id)
            account.setBalanceUSD(BigDecimal.valueOf(200).setScale(2))
            def response = new AccountResponse("Successfully added currency", account)
           1 * currencyService.addCurrency(id.toLong(), TEST_CURRENCY, TEST_AMOUNT) >> response
        } else {
          1 * currencyService.addCurrency(id.toLong(), TEST_CURRENCY, TEST_AMOUNT)
             >> { throw new AccountNotFoundException("Account with ID " + id + " not found") }
        }

        when:
        def response = mockMvc.perform(post("/api/currency/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequest))

        then:
        response.andExpect(status().is(expectedStatus))
        if (expectedStatus == 200) {
            response.andExpect(jsonPath('$.message').value("Successfully added currency"))
            response.andExpect(jsonPath('$.account.id').value(id))
            response.andExpect(jsonPath('$.account.firstName').value("name"))
            response.andExpect(jsonPath('$.account.lastName').value("lastName"))
            response.andExpect(jsonPath('$.account.balancePLN').value(100.0))
            response.andExpect(jsonPath('$.account.balanceUSD').value(200.0))
        } else if (expectedStatus == 404) {
            response.andExpect(jsonPath('$.message').value("Account with ID " + id + " not found"))
        }

        where:
        id || accountExists || expectedStatus
        1  || true          || 200
        2  || false         || 404
    }

    @Unroll
    def "test subtractCurrency with ID #id"() {
        given:
        def request = createRequest(id)
        def jsonRequest = toJson(request)

        if (accountExists) {
            def account = createAccount(id)
            account.setBalanceUSD(BigDecimal.valueOf(50).setScale(2))
            def response = new AccountResponse("Successfully subtracted currency", account)
            1 * currencyService.subtractCurrency(id.toLong(), TEST_CURRENCY, TEST_AMOUNT) >> response
        } else {
           1 *  currencyService.subtractCurrency(id.toLong(), TEST_CURRENCY, TEST_AMOUNT)
                    >> { throw new AccountNotFoundException("Account with ID " + id + " not found") }
        }

        when:
        def response = mockMvc.perform(post("/api/currency/subtract")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequest))

        then:
        response.andExpect(status().is(expectedStatus))
        if (expectedStatus == 200) {
            response.andExpect(jsonPath('$.message').value("Successfully subtracted currency"))
            response.andExpect(jsonPath('$.account.id').value(id))
            response.andExpect(jsonPath('$.account.firstName').value("name"))
            response.andExpect(jsonPath('$.account.lastName').value("lastName"))
            response.andExpect(jsonPath('$.account.balancePLN').value(100.0))
            response.andExpect(jsonPath('$.account.balanceUSD').value(50.0))
        } else if (expectedStatus == 404) {
            response.andExpect(jsonPath('$.message').value("Account with ID " + id + " not found"))
        }

        where:
        id || accountExists || expectedStatus
        1  || true          || 200
        2  || false         || 404
    }

    private static String toJson(Object object) {
        new ObjectMapper().writeValueAsString(object)
    }

    Account createAccount(int id) {
        def account = new Account()
        account.setId(id)
        account.setBalancePLN(TEST_AMOUNT)
        account.setBalanceUSD(TEST_AMOUNT)
        account.setFirstName("name")
        account.setLastName("lastName")
        account
    }

    CurrencyExchangeDTO createRequest(int id) {
        new CurrencyExchangeDTO(id, TEST_CURRENCY, TEST_AMOUNT)
    }
}

