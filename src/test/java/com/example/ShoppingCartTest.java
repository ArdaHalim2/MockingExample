package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(shoppingCart.getTotalPrice()).isEqualTo(200.0);
    }

    @Test
    void shouldRemoveProductFromCart() {
        Product product = new Product("Product 1", 100.0);

        shoppingCart.addProduct(product, 2);

        shoppingCart.removeProduct(product, 1);

        assertThat(shoppingCart.getTotalPrice()).isEqualTo(100.0);
    }

    @Test
    void shouldCalculateTotalPrice() {
        Product product1 = new Product("Product 1", 100.0);
        Product product2 = new Product("Product 2", 200.0);

        shoppingCart.addProduct(product1, 2);
        shoppingCart.addProduct(product2, 1);

        assertThat(shoppingCart.getTotalPrice()).isEqualTo(400.0);
    }

    @Test
    void shouldApplyDiscountCorrectly() {
        Product product1 = new Product("Product 1", 100.0);
        shoppingCart.addProduct(product1, 2);

        shoppingCart.applyDiscount(0.1);

        assertThat(shoppingCart.getTotalPrice()).isEqualTo(180.0);
    }

    @Test
    void shouldUpdateQuantityCorrectly() {
        Product product = new Product("Product 1", 100.0);
        shoppingCart.addProduct(product, 2);  // Lägg till 2 produkter

        shoppingCart.updateQuantity(product, 3);  // Uppdatera till 3 produkter

        assertThat(shoppingCart.getTotalPrice()).isEqualTo(300.0);  // Totalpris = 100.0 * 3
    }

}
