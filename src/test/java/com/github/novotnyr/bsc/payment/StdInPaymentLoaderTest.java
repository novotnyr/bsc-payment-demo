package com.github.novotnyr.bsc.payment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StdInPaymentLoaderTest {
    private PaymentRepository paymentRepository;

    @Before
    public void init() {
        this.paymentRepository = new PaymentRepository();
    }

    @Test
    public void testStdIn() throws IOException {
        String buffer = "USD 1000\n"
                + "USD 2000\n"
                + "quit";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.getBytes(StandardCharsets.UTF_8));

        StdInPaymentLoader loader = new StdInPaymentLoader(this.paymentRepository) {
            @Override
            protected InputStream getInputStream() {
                return inputStream;
            }
        };
        loader.load();
        Assert.assertEquals(2, this.paymentRepository.getTransactionalLogSize());
    }
}