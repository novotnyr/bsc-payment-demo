package com.github.novotnyr.bsc.payment;

import org.awaitility.Awaitility;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.is;

public class PeriodicPaymentLoggerTest {

    @Test
    public void testPeriod() {
        PaymentRepository paymentRepository = new PaymentRepository();
        paymentRepository.addPayment(new Payment("USD", new BigDecimal(20)));
        paymentRepository.addPayment(new Payment("USD", new BigDecimal(40)));

        AtomicInteger eventCount = new AtomicInteger();
        PeriodicPaymentLogger logger = new PeriodicPaymentLogger(paymentRepository, 1) {
            @Override
            protected void doLog(Collection<Payment> payments) {
                super.doLog(payments);
                eventCount.addAndGet(payments.size());
            }
        };
        logger.start();

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAtomic(eventCount, is(5));
    }
}