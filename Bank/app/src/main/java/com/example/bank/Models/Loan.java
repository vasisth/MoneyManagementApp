package com.example.bank.Models;

public class Loan {
    private int _id;
    private int user_id;
    private int transaction_id;
    private Double init_amount;
    private Double monthly_roi;
    private Double monthly_payment;
    private Double remained_amount;
    private String init_date;
    private String finish_date;
    private String name;

    public Loan() {
    }

    public Loan(int _id, int user_id, int transaction_id, Double init_amount, Double monthly_roi, Double monthly_payment, Double remained_amount, String init_date, String finish_date, String name) {
        this._id = _id;
        this.user_id = user_id;
        this.transaction_id = transaction_id;
        this.init_amount = init_amount;
        this.monthly_roi = monthly_roi;
        this.monthly_payment = monthly_payment;
        this.remained_amount = remained_amount;
        this.init_date = init_date;
        this.finish_date = finish_date;
        this.name = name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Double getInit_amount() {
        return init_amount;
    }

    public void setInit_amount(Double init_amount) {
        this.init_amount = init_amount;
    }

    public Double getMonthly_roi() {
        return monthly_roi;
    }

    public void setMonthly_roi(Double monthly_roi) {
        this.monthly_roi = monthly_roi;
    }

    public Double getMonthly_payment() {
        return monthly_payment;
    }

    public void setMonthly_payment(Double monthly_payment) {
        this.monthly_payment = monthly_payment;
    }

    public Double getRemained_amount() {
        return remained_amount;
    }

    public void setRemained_amount(Double remained_amount) {
        this.remained_amount = remained_amount;
    }

    public String getInit_date() {
        return init_date;
    }

    public void setInit_date(String init_date) {
        this.init_date = init_date;
    }

    public String getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(String finish_date) {
        this.finish_date = finish_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "_id=" + _id +
                ", user_id=" + user_id +
                ", transaction_id=" + transaction_id +
                ", init_amount=" + init_amount +
                ", monthly_roi=" + monthly_roi +
                ", monthly_payment=" + monthly_payment +
                ", remained_amount=" + remained_amount +
                ", init_date='" + init_date + '\'' +
                ", finish_date='" + finish_date + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
