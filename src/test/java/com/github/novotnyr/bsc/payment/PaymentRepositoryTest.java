package com.github.novotnyr.bsc.payment;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

public class PaymentRepositoryTest {
    @Test
    public void testAddToTransactionalLog() {
        PaymentRepository paymentRepository = new PaymentRepository();

        paymentRepository.addPayment(new Payment("USD", new BigDecimal(1000)));
        paymentRepository.addPayment(new Payment("HKD", new BigDecimal(100)));
        paymentRepository.addPayment(new Payment("USD", new BigDecimal(-100)));
        paymentRepository.addPayment(new Payment("RMB", new BigDecimal(2000)));
        paymentRepository.addPayment(new Payment("HKD", new BigDecimal(200)));

        Assert.assertEquals(5, paymentRepository.getTransactionalLogSize());
    }

    @Test
    public void testGetPayments() {
        PaymentRepository paymentRepository = new PaymentRepository();

        paymentRepository.addPayment(new Payment("USD", new BigDecimal(1000)));
        paymentRepository.addPayment(new Payment("HKD", new BigDecimal(100)));
        paymentRepository.addPayment(new Payment("USD", new BigDecimal(-100)));
        paymentRepository.addPayment(new Payment("RMB", new BigDecimal(2000)));
        paymentRepository.addPayment(new Payment("HKD", new BigDecimal(200)));

        List<Payment> payments = paymentRepository.getPayments();
        Assert.assertEquals(3, payments.size());

        Payment totalUsd = new Payment("USD", new BigDecimal(900));
        Payment totalHkd = new Payment("HKD", new BigDecimal(300));
        Payment totalRmb = new Payment("RMB", new BigDecimal(2000));

        Assert.assertTrue("Total USD is not expected value", payments.contains(totalUsd));
        Assert.assertTrue("Total HKD is not expected value",payments.contains(totalHkd));
        Assert.assertTrue("Total RMB is not expected value",payments.contains(totalRmb));
    }
}