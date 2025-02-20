package com.example.payment;

public class PaymentProcessor {
    private final PaymentApiService paymentApiService;
    private final DatabaseService databaseService;
    private final EmailService emailService;

    public PaymentProcessor(PaymentApiService paymentApiService, DatabaseService databaseService, EmailService emailService) {
        this.paymentApiService = paymentApiService;
        this.databaseService = databaseService;
        this.emailService = emailService;
    }

    public boolean processPayment(String apiKey, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Belopp måste vara större än 0");
        }

        PaymentApiResponse response = paymentApiService.charge(apiKey, amount);

        if (response.isSuccess()) {
            databaseService.savePayment(amount, "SUCCESS");
            emailService.sendPaymentConfirmation("user@example.com", amount);
            return true;
        } else {
            databaseService.savePayment(amount, "FAILED");
            return false;
        }
    }
}
