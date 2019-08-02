package com.github.novotnyr.bsc.payment;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * In-memory repository of payments. Represents a transactional log,
 * where all payments, even with duplicate currencies, are represented.
 */
public class PaymentRepository {
    private List<Payment> transactionalLog = new CopyOnWriteArrayList<>();

    /**
     * Returns a read-only view of the current payment statuses
     */
    public List<Payment> getPayments() {
        return this.transactionalLog.stream()
                .collect(Collectors.toMap(Payment::getCurrency, Payment::getAmount, BigDecimal::add))
                .entrySet()
                .stream()
                .map(entry -> new Payment(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public void addPayment(Payment payment) {
        this.transactionalLog.add(payment);
    }

    public void addPayments(Collection<Payment> payments) {
        this.transactionalLog.addAll(payments);
    }

    /**
     * Return number of all payments. Used mainly for unit testing.
     */
    protected int getTransactionalLogSize() {
        return this.transactionalLog.size();
    }
}
