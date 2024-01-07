package com.example.transactguard;

public class messageModel {
    private String creditCardNumber;
    private String merchantIdPaidTo;
    private double amountPaid;
    private String transactionStatus;

    // Constructors
    public messageModel() {
        // Default constructor
    }

    public messageModel(String creditCardNumber, String merchantIdPaidTo, double amountPaid, String transactionStatus) {
        this.creditCardNumber = creditCardNumber;
        this.merchantIdPaidTo = merchantIdPaidTo;
        this.amountPaid = amountPaid;
        this.transactionStatus = transactionStatus;
    }

    // Getters and Setters
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getMerchantIdPaidTo() {
        return merchantIdPaidTo;
    }

    public void setMerchantIdPaidTo(String merchantIdPaidTo) {
        this.merchantIdPaidTo = merchantIdPaidTo;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}
