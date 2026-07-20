package com.example.sdfguanlixt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.sdfguanlixt.entity.PaymentOrder;
import java.util.ArrayList;
import java.util.List;

public class PayResult {

    private PaymentOrder order;
    private String transactionNo;

    @JsonProperty("payMethod")
    private String payMethod;

    private String payMethodLabel;
    private double amount;
    private String payTime;
    private String verifyCode;
    private List<String> steps = new ArrayList<>();

    public PaymentOrder getOrder() {
        return order;
    }

    public void setOrder(PaymentOrder order) {
        this.order = order;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getPayMethodLabel() {
        return payMethodLabel;
    }

    public void setPayMethodLabel(String payMethodLabel) {
        this.payMethodLabel = payMethodLabel;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }
}
