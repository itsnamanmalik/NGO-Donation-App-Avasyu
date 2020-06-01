package com.glau.avasyu;

public class PaymentModel {
    private String ngo_name,amount,refid;

    public String getNgo_name() {
        return ngo_name;
    }

    public void setNgo_name(String ngo_name) {
        this.ngo_name = ngo_name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRefid() {
        return refid;
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }

    public PaymentModel(String ngo_name, String amount, String refid) {
        this.ngo_name = ngo_name;
        this.amount = amount;
        this.refid = refid;
    }

    public PaymentModel(){

    }
}
