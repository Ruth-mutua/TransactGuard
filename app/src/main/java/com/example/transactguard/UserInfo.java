package com.example.transactguard;

// UserInfo.java
public class UserInfo {
    private String name;
    private String email;
    private String cardNumber;

    public UserInfo(String name, String email, String cardNumber) {
        this.name = name;
        this.email = email;
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}

