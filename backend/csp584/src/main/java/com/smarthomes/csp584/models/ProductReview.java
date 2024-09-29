package com.smarthomes.csp584.models;

public class ProductReview {
    private String productModelName;
    private String productCategory;
    private double productPrice;
    private String storeID;
    private String storeZip;
    private String storeCity;
    private String storeState;
    private boolean productOnSale;
    private String manufacturerName;
    private boolean manufacturerRebate;
    private String userID;
    private int userAge;
    private String userGender;
    private String userOccupation;
    private int reviewRating;
    private String reviewDate;
    private String reviewText;

    public ProductReview(String productModelName, String productCategory, double productPrice, String storeID,
                         String storeZip, String storeCity, String storeState, boolean productOnSale,
                         String manufacturerName, boolean manufacturerRebate, String userID, int userAge,
                         String userGender, String userOccupation, int reviewRating, String reviewDate,
                         String reviewText) {
        this.productModelName = productModelName;
        this.productCategory = productCategory;
        this.productPrice = productPrice;
        this.storeID = storeID;
        this.storeZip = storeZip;
        this.storeCity = storeCity;
        this.storeState = storeState;
        this.productOnSale = productOnSale;
        this.manufacturerName = manufacturerName;
        this.manufacturerRebate = manufacturerRebate;
        this.userID = userID;
        this.userAge = userAge;
        this.userGender = userGender;
        this.userOccupation = userOccupation;
        this.reviewRating = reviewRating;
        this.reviewDate = reviewDate;
        this.reviewText = reviewText;
    }

    public String getProductModelName() {
        return productModelName;
    }

    public void setProductModelName(String productModelName) {
        this.productModelName = productModelName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getStoreZip() {
        return storeZip;
    }

    public void setStoreZip(String storeZip) {
        this.storeZip = storeZip;
    }

    public String getStoreCity() {
        return storeCity;
    }

    public void setStoreCity(String storeCity) {
        this.storeCity = storeCity;
    }

    public String getStoreState() {
        return storeState;
    }

    public void setStoreState(String storeState) {
        this.storeState = storeState;
    }

    public boolean isProductOnSale() {
        return productOnSale;
    }

    public void setProductOnSale(boolean productOnSale) {
        this.productOnSale = productOnSale;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public boolean isManufacturerRebate() {
        return manufacturerRebate;
    }

    public void setManufacturerRebate(boolean manufacturerRebate) {
        this.manufacturerRebate = manufacturerRebate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserOccupation() {
        return userOccupation;
    }

    public void setUserOccupation(String userOccupation) {
        this.userOccupation = userOccupation;
    }

    public int getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(int reviewRating) {
        this.reviewRating = reviewRating;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}
