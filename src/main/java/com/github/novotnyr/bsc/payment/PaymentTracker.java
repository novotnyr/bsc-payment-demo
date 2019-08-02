package com.github.novotnyr.bsc.payment;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Payment Tracker facade callable from main method.
 */
public class PaymentTracker {
    private PaymentRepository paymentRepository = new PaymentRepository();

    private File paymentFile;

    private boolean stdInEnabled = true;

    private PeriodicPaymentLogger periodicPaymentLogger;

    private UsdCurrencyConverter usdCurrencyConverter = new UsdCurrencyConverter();

    /**
     * Set an input file that contains pre-defined payments
     */
    public void setFile(File file) {
        this.paymentFile = file;
    }

    /**
     * Configures a period when periodic payment logger dumps payment information.
     */
    public void setLoggingPeriod(int periodInSeconds) {
        if (periodInSeconds > 0) {
            this.periodicPaymentLogger = new PeriodicPaymentLogger(this.paymentRepository, periodInSeconds, usdCurrencyConverter);
        }
    }

    /**
     * Configures a currency->dollar exchange rate
     */
    public void setDollarExchangeRate(String currency, BigDecimal rate) {
        this.usdCurrencyConverter.setExchangeRate(currency, rate);
    }


    /**
     * Launches the payment tracker.
     */
    public void start() {
        // start logging as a first thing, otherwise file/stdin loading might block main thread
        startPeriodicLogger();
        loadFromFile();
        loadFromStdIn();
    }

    /**
     * Starts Console payment loader, reading data line-by-line
     */
    private void loadFromStdIn() {
        if (!this.stdInEnabled) {
            return;
        }
        try {
            PaymentLoader stdInPaymentLoader = new StdInPaymentLoader(paymentRepository);
            stdInPaymentLoader.load();
        } catch (IOException e) {
            throw new PaymentTrackerException("Cannot load payments from stdin. Terminating program", e);
        }
    }

    /**
     * Preloads payments from a file.
     */
    private void loadFromFile() throws PaymentTrackerException {
        if (this.paymentFile == null) {
            return;
        }
        if (!paymentFile.exists()) {
            throw new PaymentTrackerException("File '" + paymentFile + "' does not exist.");
        }
        try {
            PaymentLoader filePaymentLoader = new FilePaymentLoader(paymentFile, paymentRepository);
            filePaymentLoader.load();
        } catch (IOException e) {
            throw new PaymentTrackerException("Cannot load payments from '" + paymentFile + "'", e);
        }
    }

    private void startPeriodicLogger() {
        if (this.periodicPaymentLogger == null) {
            return;
        }
        this.periodicPaymentLogger.start();
    }

    /**
     * Shuts down this tracker. Also stops the periodic payment logging.
     */
    private void stop() {
        if (this.periodicPaymentLogger == null) {
            return;
        }
        this.periodicPaymentLogger.stop();
    }


    /**
     * Starts the payment tracker from OS.
     * <p>
     *     There are two parameters from 'args'
     *     <li> optional file name with predefined payments
     *     <li> optional period for periodic payment logger. Default: 60 seconds.
     * </p>
     */
    public static void main(String[] args) {
        PaymentTracker paymentTracker = null;

        try {
            File paymentFile = null;
            if (args.length > 0) {
                paymentFile = new File(args[0]);
            }
            int loggingPeriod = 60; /*seconds */
            if (args.length > 1) {
                loggingPeriod = Integer.valueOf(args[1]);
            }

            paymentTracker = new PaymentTracker();
            paymentTracker.setFile(paymentFile);
            paymentTracker.setLoggingPeriod(loggingPeriod);

            paymentTracker.setDollarExchangeRate("HKD", new BigDecimal("0.128733333"));
            paymentTracker.setDollarExchangeRate("RMB", new BigDecimal("0.1573"));

            paymentTracker.start();
        } catch (Exception e) {
            System.err.println("Cannot start payment tracker: " + e.getMessage() + ". Terminating program");
            e.printStackTrace();
        } finally {
            if (paymentTracker != null) {
                paymentTracker.stop();
            }
        }
    }

}
