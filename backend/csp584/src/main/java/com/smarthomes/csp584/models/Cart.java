package com.smarthomes.csp584.models;

import java.util.List;

public class Cart {
    private int id;
    private int userId;
    private int productId;
    private String productName;
    private double productPrice;
    private int quantity;
    private boolean warrantyAdded;
    private double warrantyPrice;
    private List<Accessory> accessories;
    private double accessoriesPrice;
    private double totalPrice;

    public Cart(int id, int userId, int productId, String productName, double productPrice, int quantity, boolean warrantyAdded, double warrantyPrice, List<Accessory> accessories, double accessoriesPrice, double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.warrantyAdded = warrantyAdded;
        this.warrantyPrice = warrantyPrice;
        this.accessories = accessories;
        this.accessoriesPrice = accessoriesPrice;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isWarrantyAdded() {
        return warrantyAdded;
    }

    public void setWarrantyAdded(boolean warrantyAdded) {
        this.warrantyAdded = warrantyAdded;
    }

    public double getWarrantyPrice() {
        return warrantyPrice;
    }

    public void setWarrantyPrice(double warrantyPrice) {
        this.warrantyPrice = warrantyPrice;
    }

    public List<Accessory> getAccessories() {
        return accessories;
    }

    public void setAccessories(List<Accessory> accessories) {
        this.accessories = accessories;
    }

    public double getAccessoriesPrice() {
        return accessoriesPrice;
    }

    public void setAccessoriesPrice(double accessoriesPrice) {
        this.accessoriesPrice = accessoriesPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
