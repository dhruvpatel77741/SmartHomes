package com.smarthomes.csp584.models;

public class Transaction {
    private int transactionId;
    private int orderId;
    private int userId;
    private String customerName;
    private String shippingAddress;
    private String creditCardNumber;
    private String transactionDate;
    private double transactionAmount;
    private String paymentStatus;
    private int productId;
    private String category;
    private int quantity;
    private double shippingCost;
    private double discount;
    private double totalSales;
    private String storeAddress;

    public Transaction(int transactionId, int orderId, int userId, String customerName, String shippingAddress, String creditCardNumber,
                       String transactionDate, double transactionAmount, String paymentStatus, int productId, String category,
                       int quantity, double shippingCost, double discount, double totalSales, String storeAddress) {
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.userId = userId;
        this.customerName = customerName;
        this.shippingAddress = shippingAddress;
        this.creditCardNumber = creditCardNumber;
        this.transactionDate = transactionDate;
        this.transactionAmount = transactionAmount;
        this.paymentStatus = paymentStatus;
        this.productId = productId;
        this.category = category;
        this.quantity = quantity;
        this.shippingCost = shippingCost;
        this.discount = discount;
        this.totalSales = totalSales;
        this.storeAddress = storeAddress;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }
}
