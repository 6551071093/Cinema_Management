package com.cinema.dao;

import com.cinema.entity.Invoice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {
    
    public int createInvoice(Invoice invoice) {
        // Cập nhật query với full_name, sdt và booking_time
        String sql = "INSERT INTO INVOICE (account_id, full_name, sdt, total_amount, payment_status, booking_time) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, invoice.getAccountId());
            stmt.setString(2, invoice.getFullName());
            stmt.setString(3, invoice.getSdt());
            stmt.setDouble(4, invoice.getTotalAmount());
            stmt.setString(5, invoice.getPaymentStatus());
            stmt.setTimestamp(6, invoice.getBookingTime());
            
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public List<Invoice> getAllInvoices() {
        List<Invoice> list = new ArrayList<>();
        // Sắp xếp theo booking_time mới nhất
        String sql = "SELECT * FROM INVOICE ORDER BY booking_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(new Invoice(
                    rs.getInt("id"),
                    rs.getInt("account_id"),
                    rs.getString("full_name"),
                    rs.getString("sdt"),
                    rs.getDouble("total_amount"),
                    rs.getString("payment_status"),
                    rs.getTimestamp("booking_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}