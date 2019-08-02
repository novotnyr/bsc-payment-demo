package com.github.novotnyr.bsc.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * General payment loader that reads a specific source and parses data into {@link PaymentRepository}.
 */
public abstract class PaymentLoader {
    public static final Logger logger = LoggerFactory.getLogger(PaymentLoader.class);

    protected final PaymentRepository paymentRepository;

    protected final AppendMode appendMode;

    /**
     * Creates a loader that will add payments to the predefined payment repository.
     */
    public PaymentLoader(PaymentRepository paymentRepository) {
        this(paymentRepository, AppendMode.BATCH);
    }

    /**
     * Creates a loader that will add payments to the predefined payment repository, with specific append mode
     * @param paymentRepository destination
     */
    public PaymentLoader(PaymentRepository paymentRepository, AppendMode appendMode) {
        this.paymentRepository = Objects.requireNonNull(paymentRepository, "Payment Repository must be set");
        this.appendMode = appendMode;
    }

    /**
     * General payment data load from a dedicated resource.
     * <p>
     *  Loading has two modes:
     *  <li><i>immediate:</i> each syntactically correct payment line is immediately added to the repository
     *  <li><i>batch:</i> all lines are parsed and if all are correct, they are added to the repository in a batch.
     * </p>
     */
    public void load() throws IOException, PaymentFormatException {
        List<Payment> payments = new LinkedList<>();
        int lineNumber = 0;
        try (Scanner scanner = new Scanner(getInputStream())) {
            while (scanner.hasNextLine()) {
                try {
                    lineNumber++;
                    String line = scanner.nextLine();
                    if (isEof(line)) {
                        logger.debug("Reaching EOF with line '{}'", line);
                        break;
                    }
                    Payment payment = Payment.parse(line);
                    if (this.appendMode == AppendMode.IMMEDIATE) {
                        logger.info("Adding line '{}' to payments", line);
                        this.paymentRepository.addPayment(payment);
                    } else {
                        payments.add(Payment.parse(line));
                    }
                } catch (PaymentFormatException e) {
                    throw new PaymentFormatException("Cannot parse payment on line '" + lineNumber + "': " + e.getMessage(), e);
                }
            }
        }
        logger.info("Adding {} lines to payments", payments.size());
        this.paymentRepository.addPayments(payments);
    }

    /**
     * Indicates which line correspond to end-of-file indicator, terminating the load process.
     */
    protected boolean isEof(String line) {
        return false;
    }

    /**
     * Provide a fresh instance of input stream with payment data.
     */
    protected abstract InputStream getInputStream() throws IOException;

    /**
     * Indicates a loading mechanism.
     */
    public enum AppendMode {
        /**
         * Denotes a load-all-or-none mode of data load.
         */
        BATCH,
        /**
         * Denotes a mode where each line matches a single payment that is immediately added to the repo.
         */
        IMMEDIATE
    }
}
