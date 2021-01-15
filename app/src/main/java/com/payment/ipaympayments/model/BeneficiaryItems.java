package com.payment.ipaympayments.model;

import java.io.Serializable;

public class BeneficiaryItems implements Serializable {

    private String beneid;
    private String benename;
    private String beneaccount;
    private String benebank;
    private String beneifsc;
    private String benebankid;
    private String benestatus;
    private String benemobile;
    private String sendername;
    private String sendernumber;

    public String getBeneid() {
        return beneid;
    }

    public void setBeneid(String beneid) {
        this.beneid = beneid;
    }

    public String getBenemobile() {
        return benemobile;
    }

    public void setBenemobile(String benemobile) {
        this.benemobile = benemobile;
    }

    public String getBenename() {
        return benename;
    }

    public void setBenename(String benename) {
        this.benename = benename;
    }

    public String getBeneaccount() {
        return beneaccount;
    }

    public void setBeneaccount(String beneaccount) {
        this.beneaccount = beneaccount;
    }

    public String getBenebank() { return benebank; }

    public void setBenebank(String benebank) {
        this.benebank = benebank;
    }

    public String getBeneifsc() {
        return beneifsc;
    }

    public void setBeneifsc(String beneifsc) {
        this.beneifsc = beneifsc;
    }

    public String getBenebankid() {
        return benebankid;
    }

    public void setBenebankid(String benebankid) {
        this.benebankid = benebankid;
    }

    public String getBenestatus() {
        return benestatus;
    }

    public void setBenestatus(String benestatus) {
        this.benestatus = benestatus;
    }

    public String getSendernumber() {
        return sendernumber;
    }

    public void setSendernumber(String sendernumber) {
        this.sendernumber = sendernumber;
    }

    public String getSendername() { return sendername; }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }
}
