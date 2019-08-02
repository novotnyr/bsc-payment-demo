package com.github.novotnyr.bsc.payment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FilePaymentLoaderTest {

    private PaymentRepository paymentRepository;

    @Before
    public void init() {
        this.paymentRepository = new PaymentRepository();
    }

    @Test
    public void testLoad() throws IOException {
        String fileName = getClass().getResource("/payments.txt").getFile();

        FilePaymentLoader filePaymentLoader = new FilePaymentLoader(new File(fileName), this.paymentRepository);
        filePaymentLoader.load();

        Assert.assertEquals(5, this.paymentRepository.getTransactionalLogSize());
    }
}