package com.example.foodapp.Model;

public class Receipt {
    private int receiptid;
    private String date,time,tableno;
    private boolean payment,delete;

    public Receipt(int receiptid, boolean delete, String date, String time, String tableno, boolean payment) {
        this.receiptid = receiptid;
        this.delete = delete;
        this.date = date;
        this.time = time;
        this.tableno = tableno;
        this.payment = payment;
    }

    public int getReceiptid() {
        return receiptid;
    }

    public boolean getDelete() {
        return delete;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTableno() {
        return tableno;
    }

    public boolean isPayment() {
        return payment;
    }
}
