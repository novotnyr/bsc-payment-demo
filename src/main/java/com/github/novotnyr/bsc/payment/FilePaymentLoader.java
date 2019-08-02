package com.github.novotnyr.bsc.payment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Payment loader which reads data from a filesystem file.
 * <p>
 *     This loader works in batch mode -- either all payments or no payments are loaded.
 * </p>
 */
public class FilePaymentLoader extends PaymentLoader {
    private final File file;

    public FilePaymentLoader(File file, PaymentRepository paymentRepository) {
        super(paymentRepository, AppendMode.BATCH);
        this.file = Objects.requireNonNull(file, "File must be set");
    }

    /**
     * Loads data from the specific file.
     * <p>
     *     The loading method reads in the transaction-like manner. Either all
     *     data from a file are loaded or no data are loaded if any of the lines
     *     is unparsable.
     * </p>
     */
    public void load() throws IOException, PaymentFormatException {
        logger.info("Loading payments from {}", this.file);
        super.load();
    }

    @Override
    protected InputStream getInputStream() throws IOException  {
        return new FileInputStream(this.file);
    }
}
