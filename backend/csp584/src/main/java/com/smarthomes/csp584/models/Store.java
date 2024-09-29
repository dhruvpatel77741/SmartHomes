package com.smarthomes.csp584.models;

public class Store {
    private int storeId;
    private String street;
    private String city;
    private String state;
    private String zipCode;

    public Store(int storeId, String street, String city, String state, String zipCode) {
        this.storeId = storeId;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public int getStoreId() {
        return storeId;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }
}
