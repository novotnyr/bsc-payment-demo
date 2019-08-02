package com.github.novotnyr.bsc.payment;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class UsdCurrencyConverter {
    private Map<String /*currency*/, BigDecimal /*rate*/> exchangeRates = new LinkedHashMap<>();

    public void setExchangeRate(String currency, BigDecimal rate) {
        Objects.requireNonNull(currency, "Currency must be set");
        Objects.requireNonNull(rate, "Rate must be set");

        this.exchangeRates.put(currency, rate);
    }

    public Optional<BigDecimal> toUsd(Payment payment) {
        Objects.requireNonNull(payment, "Payment must be set");
        return Optional.ofNullable(this.exchangeRates.get(payment.getCurrency()))
                .map(exchangeRate -> payment.getAmount().multiply(exchangeRate));
    }
}
