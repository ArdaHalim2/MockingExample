package com.example.payment;

public interface PaymentApiService {
    PaymentApiResponse charge(String apiKey, double amount);
}
