package com.cinema.entity;

import java.sql.Timestamp;

public class Invoice {
    private int id;
    private int accountId; // Theo image_ff9537.png là số
    private String fullName;
    private String sdt;
    private double totalAmount;
    private String paymentStatus;
    private Timestamp bookingTime; // Đổi tên cho khớp image_ff9537.png

    // Constructor cập nhật
    public Invoice(int id, int accountId, String fullName, String sdt, double totalAmount, String paymentStatus, Timestamp bookingTime) {
        this.id = id;
        this.accountId = accountId;
        this.fullName = fullName;
        this.sdt = sdt;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.bookingTime = bookingTime;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getSdt() {
		return sdt;
	}

	public void setSdt(String sdt) {
		this.sdt = sdt;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Timestamp getBookingTime() {
		return bookingTime;
	}

	public void setBookingTime(Timestamp bookingTime) {
		this.bookingTime = bookingTime;
	}

    
}