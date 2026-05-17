package com.cinema.dao;

import com.cinema.entity.Ticket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {
    public List<String> getBookedSeats(int showtimeId) {
        List<String> bookedSeats = new ArrayList<>();
        String sql = "SELECT seat_number FROM TICKET WHERE showtime_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, showtimeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookedSeats.add(rs.getString("seat_number"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookedSeats;
    }

    public boolean addTicket(Ticket ticket) {
        String sql = "INSERT INTO TICKET (invoice_id, showtime_id, seat_number) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticket.getInvoiceId());
            stmt.setInt(2, ticket.getShowtimeId());
            stmt.setString(3, ticket.getSeatNumber());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<String> getOccupiedSeats(int showtimeId) {
        List<String> seats = new ArrayList<>();
        String sql = "SELECT seat_number FROM TICKET WHERE showtime_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seats.add(rs.getString("seat_number"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return seats;
    }
}