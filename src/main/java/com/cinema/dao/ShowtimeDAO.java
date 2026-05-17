package com.cinema.dao;

import com.cinema.entity.Showtime;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class ShowtimeDAO {
    

     //Lấy danh sách suất chiếu theo ID phim
    public List<Showtime> getShowtimesByMovie(int movieId) {
        List<Showtime> list = new ArrayList<>();
        String sql = "SELECT id, movie_id, room_id, start_time, end_time, ticket_price FROM SHOWTIME WHERE movie_id = ? ORDER BY start_time ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int mId = rs.getInt("movie_id");
                    int rId = rs.getInt("room_id");
                    Timestamp start = rs.getTimestamp("start_time");
                    Timestamp end = rs.getTimestamp("end_time");
                    double price = rs.getDouble("ticket_price");

                    list.add(new Showtime(id, mId, rId, start, end, price));
                }
            }
        } catch (SQLException e) {
            System.err.println("LỖI SQL KHI LẤY LỊCH CHIẾU: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
	
    /**
     * Lấy danh sách các mã ghế đã được đặt mua (PAID) của một suất chiếu cụ thể
     */
    public List<String> getBookedSeats(int showtimeId) {
        List<String> listBookedSeats = new ArrayList<>();
        String sql = "SELECT seat_number FROM TICKET WHERE showtime_id = ? AND status = 'PAID'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listBookedSeats.add(rs.getString("seat_number"));
                }
            }
        } catch (SQLException e) {
            System.err.println("LỖI SQL KHI LẤY GHẾ ĐÃ ĐẶT: " + e.getMessage());
            e.printStackTrace();
        }
        return listBookedSeats;
    }


     //Thêm suất chiếu mới
    public boolean addShowtime(Showtime st) {
        String sql = "INSERT INTO SHOWTIME (movie_id, room_id, start_time, end_time, ticket_price) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, st.getMovieId());
            ps.setInt(2, st.getRoomId());
            ps.setTimestamp(3, st.getStartTime());
            ps.setTimestamp(4, st.getEndTime());
            ps.setDouble(5, st.getTicketPrice());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    

     //Xóa suất chiếu
    public boolean deleteShowtime(int id) {
        String sql = "DELETE FROM SHOWTIME WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
