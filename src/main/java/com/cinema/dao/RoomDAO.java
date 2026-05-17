package com.cinema.dao;

import com.cinema.entity.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
	public List<Room> getAllRooms() {
	    List<Room> list = new ArrayList<>();
	    String sql = "SELECT * FROM ROOM"; 
	    try (Connection conn = DatabaseConnection.getConnection();
	         Statement st = conn.createStatement();
	         ResultSet rs = st.executeQuery(sql)) {
	        while (rs.next()) {
	            Room r = new Room(
	                rs.getInt("id"),
	                rs.getString("name"),
	                rs.getInt("num_rows"), // Đã sửa từ row_count
	                rs.getInt("num_cols"), // Đã sửa từ col_count
	                rs.getString("status") // Đảm bảo cột này tồn tại
	            );
	            list.add(r);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}
    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM ROOM WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Room(rs.getInt("id"), rs.getString("name"), 
                               rs.getInt("num_rows"), rs.getInt("num_cols"), rs.getString("status"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
    
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO ROOM (name, num_rows, num_cols, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, room.getName());
            ps.setInt(2, room.getNumRows());
            ps.setInt(3, room.getNumCols());
            ps.setString(4, room.getStatus());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu dữ liệu đã nạp vào MySQL thành công
            
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm phòng chiếu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteRoom(int id) {
        String sql = "DELETE FROM ROOM WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi xóa phòng: " + e.getMessage());
            return false; 
        }
    }
    
    public boolean updateRoom(Room room) {
        String sql = "UPDATE ROOM SET name = ?, num_rows = ?, num_cols = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, room.getName());
            ps.setInt(2, room.getNumRows());
            ps.setInt(3, room.getNumCols());
            ps.setString(4, room.getStatus());
            ps.setInt(5, room.getId());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
