package com.github.novotnyr.bsc.payment;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PaymentTest {
    @Test
    public void testParsePayment() {
        Payment payment = Payment.parse("USD 1000");
        Assert.assertEquals(new BigDecimal(1000), payment.getAmount());
        Assert.assertEquals("USD", payment.getCurrency());
    }

    @Test(expected = PaymentFormatException.class)
    public void testFailedParsePaymentWithMissingAmount() {
        Payment.parse("USD");
    }

    @Test(expected = PaymentFormatException.class)
    public void testFailedParsePaymentWithIncorrectAmount() {
        Payment.parse("USD DOLLARS");
    }
}