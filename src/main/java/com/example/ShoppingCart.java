package com.example;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private final Map<Product, Integer> products = new HashMap<>();
    private double discount = 0.0;

    public void addProduct(Product product, int quantity) {
        products.put(product, products.getOrDefault(product, 0) + quantity);
    }

    public void removeProduct(Product product, int quantity) {
        int currentQuantity = products.getOrDefault(product, 0);
        if (currentQuantity > quantity) {
            products.put(product, currentQuantity - quantity);
        } else {
            products.remove(product);
        }
    }

    public void updateQuantity(Product product, int newQuantity) {
        if (newQuantity > 0) {
            products.put(product, newQuantity);
        } else {
            products.remove(product);
        }
    }

}
