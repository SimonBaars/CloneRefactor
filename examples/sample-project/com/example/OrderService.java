package com.example;

public class OrderService {
    
    // This method has a clone in CustomerService
    public void processNewOrder(Order order) {
        validateOrder(order);
        checkInventory(order);
        calculateTotal(order);
        applyDiscounts(order);
        saveToDatabase(order);
        sendConfirmation(order);
    }
    
    // This method has a clone in SubscriptionService
    public void processOrderUpdate(Order order) {
        validateOrder(order);
        checkInventory(order);
        calculateTotal(order);
        applyDiscounts(order);
        updateDatabase(order);
        sendUpdateNotification(order);
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
    
    private void updateDatabase(Order order) {
        // Update logic
    }
    
    private void sendConfirmation(Order order) {
        // Send confirmation email
    }
    
    private void sendUpdateNotification(Order order) {
        // Send update notification
    }
}
