package com.github.novotnyr.bsc.payment;

import java.math.BigDecimal;
import java.util.Objects;

public class Payment {
    private final String currency;

    private final BigDecimal amount;

    public Payment(String currency, BigDecimal amount) {
        this.currency = Objects.requireNonNull(currency, "Currency must be set");
        this.amount = Objects.requireNonNull(amount, "Amount must be set");
    }

    public static Payment parse(String value) throws PaymentFormatException {
        if (value == null) {
            throw new PaymentFormatException("Value must be provided");
        }
        String[] components = value.split(" ");
        if (components.length < 2) {
            throw new PaymentFormatException("Syntax error in value '" + value + "'. Not enough parameters");
        }
        BigDecimal amount = parseAmount(components[1]);
        return new Payment(components[0], amount);
    }


    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return this.currency + " " + this.amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment payment = (Payment) o;
        return getCurrency().equals(payment.getCurrency()) &&
                getAmount().equals(payment.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrency(), getAmount());
    }

    private static BigDecimal parseAmount(String amountString) {
        try {
            return new BigDecimal(amountString);
        } catch (NumberFormatException e) {
            throw new PaymentFormatException("Syntax error in amount '" + amountString + "'");
        }
    }
}
