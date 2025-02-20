package com.example.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

class PaymentProcessorTest {

    @Mock
    private PaymentApiService paymentApiService;

    @Mock
    private DatabaseService databaseService;

    @Mock
    private EmailService emailService;

    private PaymentProcessor paymentProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentProcessor = new PaymentProcessor(paymentApiService, databaseService, emailService);
    }

    @Test
    void shouldProcessPaymentSuccessfully() {
        when(paymentApiService.charge(anyString(), anyDouble()))
                .thenReturn(new PaymentApiResponse(true));

        boolean result = paymentProcessor.processPayment("test-api-key", 100.0);

        assertThat(result).isTrue();
        verify(databaseService).savePayment(100.0, "SUCCESS");
        verify(emailService).sendPaymentConfirmation("user@example.com", 100.0);
    }

    @Test
    void shouldReturnFalseWhenPaymentFails() {
        when(paymentApiService.charge(anyString(), anyDouble()))
                .thenReturn(new PaymentApiResponse(false));

        boolean result = paymentProcessor.processPayment("test-api-key", 100.0);

        assertThat(result).isFalse();
        verify(databaseService).savePayment(100.0, "FAILED");
        verify(emailService, never()).sendPaymentConfirmation(anyString(), anyDouble());
    }

    @Test
    void shouldThrowExceptionForInvalidAmount() {
        assertThatThrownBy(() -> paymentProcessor.processPayment("test-api-key", -50.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Belopp måste vara större än 0");
    }
}
