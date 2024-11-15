package com.example.currencyexchange.controller

import com.example.currencyexchange.errorhandling.AccountNotFoundException
import com.example.currencyexchange.errorhandling.ErrorHandler
import com.example.currencyexchange.model.Account
import com.example.currencyexchange.model.CreateAccountDTO
import com.example.currencyexchange.service.AccountService
import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
class AccountControllerTest extends Specification {

    @Autowired
    AccountController accountController

    @SpringBean
    AccountService accountService = Mock()

    @Autowired
    ErrorHandler errorHandler

    MockMvc mockMvc

    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .setControllerAdvice(errorHandler)
                .build()
    }

    @Unroll
    def "test createAccount with #description"() {
        given:
        def request = new CreateAccountDTO(firstName: firstName, lastName: lastName, initialPlnBalance: initialPlnBalance)

        when:
        def response = mockMvc.perform(post("/api/account/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))

        then:
        response.andExpect(status().is(expectedStatus))

        where:
        description                | firstName | lastName | initialPlnBalance      || expectedStatus
        "valid request"            | "Jan"     | "Paweł"  | new BigDecimal("1000") || 200
        "missing first name"       | ""        | "Paweł"  | new BigDecimal("1000") || 400
        "missing last name"        | "Jan"     | ""       | new BigDecimal("1000") || 400
        "null initial balance"     | "Jan"     | "Paweł"  | null                   || 400
        "negative initial balance" | "Jan"     | "Paweł"  | new BigDecimal("-100") || 400
        "zero initial balance"     | "Jan"     | "Paweł"  | new BigDecimal("0")    || 400
    }

    @Unroll
    def "test getAccount with ID #id"() {
        given:
        accountService.getAccount(id) >> Optional.ofNullable(mockAccount)

        when:
        def response = mockMvc.perform(get("/api/account/$id"))

        then:
        response.andExpect(status().is(expectedStatus))

        where:
        id || mockAccount         || expectedStatus
        1  || new Account(id: 1L) || 200
        2  || null                || 404
    }

    @Unroll
    def "test deleteAccount with ID #id"() {
        given:
        if (accountExists) {
            1 * accountService.deleteAccount(id.toLong())
        } else {
           1 * accountService.deleteAccount(id) >>
                    {throw new AccountNotFoundException("Account with ID " + id + " not found")}
        }

        when:
        def response = mockMvc.perform(delete("/api/account/delete/$id")
                .accept(MediaType.APPLICATION_JSON))

        then:
        response.andExpect(status().is(expectedStatus))
        if (expectedStatus == 200) {
            response.andExpect(jsonPath('$.message').value("Successfully deleted account"))
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
}
