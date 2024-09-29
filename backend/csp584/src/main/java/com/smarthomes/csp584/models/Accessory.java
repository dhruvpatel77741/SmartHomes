package com.smarthomes.csp584.models;

public class Accessory {
    private int id;
    private int productId;
    private String name;
    private double price;

    public Accessory(int id, int productId, String name, double price) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
