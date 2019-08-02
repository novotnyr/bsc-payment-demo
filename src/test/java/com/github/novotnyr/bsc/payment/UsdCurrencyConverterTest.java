package com.github.novotnyr.bsc.payment;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;

public class UsdCurrencyConverterTest {
    @Test
    public void testConversionWithNoConversionRates() {
        UsdCurrencyConverter currencyConverter = new UsdCurrencyConverter();
        Optional<BigDecimal> dollars = currencyConverter.toUsd(Payment.parse("SKK 100"));
        Assert.assertFalse(dollars.isPresent());
    }

    @Test
    public void testConversion() {
        UsdCurrencyConverter currencyConverter = new UsdCurrencyConverter();
        currencyConverter.setExchangeRate("SKK", new BigDecimal("30.1260"));

        Optional<BigDecimal> dollars = currencyConverter.toUsd(Payment.parse("SKK 100"));
        Assert.assertTrue(dollars.isPresent());
        Assert.assertEquals(new BigDecimal("3012.60").byteValue(), dollars.get().byteValue());
    }
}