package com.example.currencyexchange.utils;

import org.springframework.core.convert.converter.Converter;
import com.example.currencyexchange.model.CurrencyType;

public class StringToEnumConverter implements Converter<String, CurrencyType> {
    @Override
    public CurrencyType convert(String source) {
        return CurrencyType.valueOf(source.toUpperCase());
    }
}
