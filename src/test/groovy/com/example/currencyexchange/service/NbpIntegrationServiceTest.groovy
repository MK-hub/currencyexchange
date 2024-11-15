package com.example.currencyexchange.service

import com.example.currencyexchange.model.ExchangeRates
import com.example.currencyexchange.model.Rate
import com.example.currencyexchange.model.Rates
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(NbpIntegrationService.class)
class NbpIntegrationServiceTest extends Specification {

    @Autowired
    MockRestServiceServer server

    @Autowired
    NbpIntegrationService nbpIntegrationService

    @Value('${nbp.api.url}')
    String nbpApiUrl


    def "should get exchange rate"() {
        given: "prepare exchange rates data"
        def exRates = new ExchangeRates()
        def rates = new Rates()
        def rateList = [new Rate(new BigDecimal("4.4"), new BigDecimal("4.2"))]
        rates.setRate(rateList)
        exRates.setRates(rates)

        and: "convert to XML"
        def xmlContent = convertToXml(exRates)

        and: "mock server response"
        server
                .expect(requestTo(nbpApiUrl))
                .andRespond(withSuccess(xmlContent, MediaType.APPLICATION_XML))

        when: "call getExchangeRate"
        def response = nbpIntegrationService.getExchangeRate()

        then: "verify the response"
        response.getBid() == new BigDecimal("4.4")
        response.getAsk() == new BigDecimal("4.2")
    }

    private static String convertToXml(ExchangeRates exchangeRates) {
        def context = JAXBContext.newInstance(ExchangeRates.class)
        def marshaller = context.createMarshaller()
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)

        def writer = new StringWriter()
        marshaller.marshal(exchangeRates, writer)
        return writer.toString()
    }
}
