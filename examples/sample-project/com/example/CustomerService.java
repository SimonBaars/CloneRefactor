package com.example;

public class CustomerService {
    
    // This is a clone of processNewOrder in OrderService
    public void createCustomerOrder(Order order) {
        validateOrder(order);
        checkInventory(order);
        calculateTotal(order);
        applyDiscounts(order);
        saveToDatabase(order);
        sendConfirmation(order);
    }
    
    private void validateOrder(Order order) {
        if (order == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Invalid order");
        }
    }
    
    private void checkInventory(Order order) {
        // Check inventory logic
    }
    
    private void calculateTotal(Order order) {
        // Calculate total logic
    }
    
    private void applyDiscounts(Order order) {
        // Apply discounts logic
    }
    
    private void saveToDatabase(Order order) {
        // Save logic
    }
    
    private void sendConfirmation(Order order) {
        // Send confirmation email
    }
}
