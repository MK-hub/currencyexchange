package com.example.currencyexchange.service;

import com.example.currencyexchange.errorhandling.NBPIntegrationException;
import com.example.currencyexchange.logging.TrackTime;
import com.example.currencyexchange.model.ExchangeRates;
import com.example.currencyexchange.model.Rate;
import com.example.currencyexchange.model.Rates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import static com.example.currencyexchange.config.cache.RedisConfig.CUSTOM_GENERATOR;
import static com.example.currencyexchange.config.cache.RedisConfig.EXCHANGE_CACHE;

@Service
public class NbpIntegrationService {

    @Value("${nbp.api.url}")
    private String nbpApiUrl;

    private final RestClient restClient;

    public NbpIntegrationService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .messageConverters(converters -> converters.add(new Jaxb2RootElementHttpMessageConverter()))
                .build();
        restClient.head().accept(MediaType.APPLICATION_XML);
    }

    @TrackTime
    @Cacheable(value = EXCHANGE_CACHE, keyGenerator = CUSTOM_GENERATOR)
    public Rate getExchangeRate() {
        ExchangeRates exchangeRates = fetchExchangeRatesFromApi();
        return extractExchangeRate(exchangeRates);
    }
    private ExchangeRates fetchExchangeRatesFromApi() {
        return restClient.get()
                .uri(nbpApiUrl)
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new NBPIntegrationException(
                            response.getStatusCode(),
                            response.getHeaders(),
                            "Failed to fetch exchange rates from NBP API"
                    );
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new NBPIntegrationException(
                            response.getStatusCode(),
                            response.getHeaders(),
                            "NBP API server error"
                    );
                })
                .body(ExchangeRates.class);
    }

    private Rate extractExchangeRate(ExchangeRates exchangeRates) {
        Assert.notNull(exchangeRates, "Exchange rates response cannot be null");
        return Optional.ofNullable(exchangeRates.getRates())
                .map(Rates::getRate)
                .filter(rates -> !rates.isEmpty())
                .map(List::getFirst)
                .orElseThrow(() -> new NBPIntegrationException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        HttpHeaders.EMPTY,
                        "No exchange rate data available in the response"
                ));
    }
}

