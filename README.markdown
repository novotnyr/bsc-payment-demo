About
=====
This is a BSC Payment Tracker Demo Assignment.

Building
========

    mvn package

This creates two jars:

- one standalone JAR
- one uber-JAR with all dependencies packed into a single place

Dependencies
------------
The only dependency is `slf4j` for logging.

`Logback` logging backend is optional.

Running
=======

Assuming that the Maven build has been completed, the following command runs the full Payment Tracker


    java -jar target/bsc-payment-demo-1.0-SNAPSHOT-jar-with-dependencies.jar src/test/resources/payments.txt 10

- first optional argument indicates a file name with predefined payments
- second optional argument indicates a period in seconds for payment dump

Design
======

- `PaymentTracker` is a high-level facade to the system reachable from commandline. This is a main class, too.
- `PeriodicPaymentLogger` handles periodic logging of payments.
- `PaymentLoader` is a general class for parsing payment data from various sources. There are two concrete implementations
    - `StdInPaymentLoader` that loads data from Console / `System.in`.
    - `FilePaymentLoader` adapted to load data from a specific file
- `PaymentRepository` is an in-memory repository is also a transactional log (unlike database). This allows immutable payments. If there are memory-constraints, we should not be using this anyways, since it's more reasonable to go for database.
- `UsdCurrencyConverter` handles currency-to-dollar conversions when dumping currencies to console

Payment Loaders
---------------
Payment loader can work in two modes: either *batch* (all-or-none loading) or *immediate* (add-each-line). This simplifies common code that handles parsing and reading of payment data.

`StdInPaymentLoader` is an immediate loader: any failure terminates the loading process, but already loaded payments are retained. If used inside `PaymentTracker`, the whole program is terminated.

`FilePaymentLoader` is a batch loader: any failure ignores previous loaded payment data.

Assumptions and Design Decisions
================================

- *fail-fast*: any error in the payment format immediately fails the program
- *BigDecimals*: improving on spec, we use `BigDecimal` number representation
- *threading*: periodic checker runs in a separate thread. Console and file I/O do not.
- *logging*: logging via `slf4j` and `logback` is enabled by default. Use custom `logback.xml` to prevent various logging statements.