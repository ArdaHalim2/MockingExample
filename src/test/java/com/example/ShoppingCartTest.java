package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ShoppingCartTest {

    private ShoppingCart shoppingCart;

    @BeforeEach
    void setUp() {
        shoppingCart = new ShoppingCart();
    }

    @Test
    void shouldAddProductToCart() {
        Product product = new Product("Product 1", 100.0);

        shoppingCart.addProduct(product, 2);

        asssertThat(shoppingCart.getTotalPrice()).isEqualTo(200.0);
    }

    @Test
    void shouldRemoveProductFromCart() {
        Product product = new Product("Product 1", 100.0);

        shoppingCart.addProduct(product, 2);

        shoppingCart.removeProduct(product, 1);

        assertThat(shoppingCart.getTotalPrice()).isEqualTo(100.0);
    }
}
