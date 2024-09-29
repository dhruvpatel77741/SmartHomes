package com.smarthomes.csp584.models;

import java.util.List;

public class Product {
    private int id;
    private String name;
    private String description;
    private String category;
    private double price;
    private boolean specialDiscount;
    private double discountPrice;
    private boolean manufacturerRebate;
    private double rebatePrice;
    private boolean warranty;
    private double warrantyPrice;
    private int likes;
    private List<Accessory> accessories;

    public Product(int id, String name, String description, String category, double price,
                   boolean specialDiscount, double discountPrice, boolean manufacturerRebate,
                   double rebatePrice, boolean warranty, double warrantyPrice, int likes, List<Accessory> accessories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.specialDiscount = specialDiscount;
        this.discountPrice = discountPrice;
        this.manufacturerRebate = manufacturerRebate;
        this.rebatePrice = rebatePrice;
        this.warranty = warranty;
        this.warrantyPrice = warrantyPrice;
        this.likes = likes;
        this.accessories = accessories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSpecialDiscount() {
        return specialDiscount;
    }

    public void setSpecialDiscount(boolean specialDiscount) {
        this.specialDiscount = specialDiscount;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public boolean isManufacturerRebate() {
        return manufacturerRebate;
    }

    public void setManufacturerRebate(boolean manufacturerRebate) {
        this.manufacturerRebate = manufacturerRebate;
    }

    public double getRebatePrice() {
        return rebatePrice;
    }

    public void setRebatePrice(double rebatePrice) {
        this.rebatePrice = rebatePrice;
    }

    public boolean isWarranty() {
        return warranty;
    }

    public void setWarranty(boolean warranty) {
        this.warranty = warranty;
    }

    public double getWarrantyPrice() {
        return warrantyPrice;
    }

    public void setWarrantyPrice(double warrantyPrice) {
        this.warrantyPrice = warrantyPrice;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<Accessory> getAccessories() {
        return accessories;
    }

    public void setAccessories(List<Accessory> accessories) {
        this.accessories = accessories;
    }
}
