package com.github.novotnyr.bsc.payment;

/**
 * Indicates a high-level payment tracker exception, for example for failed data load.
 */
public class PaymentTrackerException extends RuntimeException {

    public PaymentTrackerException(String msg) {
        super(msg);
    }

    public PaymentTrackerException(String message, Throwable cause) {
        super(message, cause);
    }
}