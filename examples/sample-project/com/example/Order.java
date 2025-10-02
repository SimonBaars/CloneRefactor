package com.example;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String id;
    private List<String> items = new ArrayList<>();
    private double total;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public List<String> getItems() {
        return items;
    }
    
    public void setItems(List<String> items) {
        this.items = items;
    }
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
}
