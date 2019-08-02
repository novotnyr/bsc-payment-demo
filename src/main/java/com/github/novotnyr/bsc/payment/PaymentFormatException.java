package com.github.novotnyr.bsc.payment;

/**
 * Indicates an invalid or malformed payment representation when reading from an input source.
 */
public class PaymentFormatException extends RuntimeException {

    public PaymentFormatException(String msg) {
        super(msg);
    }

    public PaymentFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}