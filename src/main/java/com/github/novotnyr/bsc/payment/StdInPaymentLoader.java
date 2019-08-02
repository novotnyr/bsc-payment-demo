package com.github.novotnyr.bsc.payment;

import java.io.InputStream;


/**
 * Payment loader that loads {@link Payment}s from {@code stdin}.
 */
public class StdInPaymentLoader extends PaymentLoader {
    public StdInPaymentLoader(PaymentRepository paymentRepository) {
        super(paymentRepository, AppendMode.IMMEDIATE);
    }

    /**
     * Provide input stream from standard input.
     */
    @Override
    protected InputStream getInputStream() {
        return System.in;
    }

    /**
     * Treat <code>quit</code> line as end of stream.
     */
    @Override
    protected boolean isEof(String line) {
        return "quit".equalsIgnoreCase(line.trim());
    }
}
