package com.cinema.entity;

public class Ticket {
    private int id;
    private int invoiceId;
    private int showtimeId;
    private String seatNumber;

    public Ticket() {}

    public Ticket(int id, int invoiceId, int showtimeId, String seatNumber) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.showtimeId = showtimeId;
        this.seatNumber = seatNumber;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }
    
    public int getShowtimeId() { return showtimeId; }
    public void setShowtimeId(int showtimeId) { this.showtimeId = showtimeId; }
    
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
}