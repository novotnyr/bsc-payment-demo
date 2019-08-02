package com.github.novotnyr.bsc.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Logger that periodically dumps content of the Payment Repository into dedicated channel.
 * <p>
 *     Note that this is one-off class that allows only one start-stop cycle.
 * </p>
 */
public class PeriodicPaymentLogger {
    public static final Logger logger = LoggerFactory.getLogger(PeriodicPaymentLogger.class);

    private final PaymentRepository paymentRepository;

    private final int periodInSeconds;

    private final ScheduledExecutorService executorService;

    private final UsdCurrencyConverter usdCurrencyConverter;

    /**
     * Create periodic logger that pulls payments from a specific repository, with default currency converter
     * @param paymentRepository repository that is a source of payments
     * @param periodInSeconds period that states how often to check for payments.
     */

    public PeriodicPaymentLogger(PaymentRepository paymentRepository, int periodInSeconds) {
        this(paymentRepository, periodInSeconds, new UsdCurrencyConverter());
    }

    /**
     * Create periodic logger that pulls payments from a specific repository.
     * @param paymentRepository repository that is a source of payments
     * @param periodInSeconds period that states how often to check for payments.
     * @param usdCurrencyConverter currency to dollars currency converter
     */
    public PeriodicPaymentLogger(PaymentRepository paymentRepository, int periodInSeconds, UsdCurrencyConverter usdCurrencyConverter) {
        this.paymentRepository = paymentRepository;
        this.periodInSeconds = periodInSeconds;
        this.usdCurrencyConverter = usdCurrencyConverter;
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    /**
     * Starts periodic checker.
     */
    public void start() {
        if (this.executorService.isShutdown()) {
            throw new IllegalStateException("Executor service has been shut down. This instance is not reusable");
        }
        logger.info("Starting periodic checks with {} {}", this.periodInSeconds, TimeUnit.SECONDS);
        this.executorService.scheduleAtFixedRate(this::run, 0, this.periodInSeconds, TimeUnit.SECONDS);
    }

    /**
     * Method that contains task for periodic checks.
     * <p>
     *     <b>Important:</b> this method runs in a separate thread!
     * </p>
     */
    protected void run() {
        List<Payment> payments = paymentRepository.getPayments();
        doLog(payments);
    }

    /**
     * Logs a collection of payments.
     * <p>
     *     <b>Important:</b> this method runs in a separate thread!
     * </p>
     */
    protected void doLog(Collection<Payment> payments) {
        logger.info("{} payments are available", payments.size());
        for (Payment payment : payments) {
            doLog(payment);
        }
    }

    /**
     * Logs a single payment in the periodic check.
     * <p>
     *     <b>Important:</b> this method runs in a separate thread!
     * </p>
     */
    protected void doLog(Payment payment) {
        if (BigDecimal.ZERO.equals(payment.getAmount())) {
            return;
        }
        String usd = usdCurrencyConverter.toUsd(payment)
                .map(amount -> " (" + amount.setScale(2, RoundingMode.HALF_UP) + " USD)")
                .orElse("");
        System.out.println(payment + usd);
    }

    /**
     * Shuts down periodic checks, including nested periodic task handler.
     */
    public void stop() {
        this.executorService.shutdownNow();
    }
}
