package com.smarthomes.csp584.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String name;
    private String userType;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String creditCardNumber;
    private String expiryDate;
    private String cvv;

    public User(int id, String username, String password, String name, String userType) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.userType = userType;
    }

    public User(int id, String username, String password, String name, String userType,
                String phone, String addressLine1, String addressLine2, String city,
                String state, String zipCode, String creditCardNumber, String expiryDate, String cvv) {
        this(id, username, password, name, userType);
        setPhone(phone);
        setAddressLine1(addressLine1);
        setAddressLine2(addressLine2);
        setCity(city);
        setState(state);
        setZipCode(zipCode);
        setCreditCardNumber(creditCardNumber);
        setExpiryDate(expiryDate);
        setCvv(cvv);
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getUserType() { return userType; }
    public String getPhone() { return phone; }
    public String getAddressLine1() { return addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public String getCreditCardNumber() { return creditCardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public String getCvv() { return cvv; }

    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setUserType(String userType) { this.userType = userType; }

    public void setPhone(String phone) {
        if (phone == null || phone.matches("\\d{10}")) {
            this.phone = phone;
        } else {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public void setCity(String city) { this.city = city; }

    public void setState(String state) { this.state = state; }

    public void setZipCode(String zipCode) {
        if (zipCode == null || zipCode.matches("\\d{5}")) {
            this.zipCode = zipCode;
        } else {
            throw new IllegalArgumentException("Invalid ZIP code format");
        }
    }

    public void setCreditCardNumber(String creditCardNumber) {
        if (creditCardNumber == null || creditCardNumber.matches("\\d{16}")) {
            this.creditCardNumber = creditCardNumber;
        } else {
            throw new IllegalArgumentException("Invalid credit card number format");
        }
    }

    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public void setCvv(String cvv) {
        if (cvv == null || cvv.matches("\\d{3}")) {
            this.cvv = cvv;
        } else {
            throw new IllegalArgumentException("Invalid CVV format");
        }
    }
}

