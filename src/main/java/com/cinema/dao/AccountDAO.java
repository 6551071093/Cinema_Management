package com.cinema.dao;

import com.cinema.entity.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

public class AccountDAO {

    public Account findByUsername(String username) {
        String sql = "SELECT * FROM ACCOUNT WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm tài khoản theo username: " + e.getMessage());
        }
        return null;
    }

    // 1.Lấy danh sách tất cả tài khoản
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM ACCOUNT";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                accounts.add(new Account(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }
    // THÊM TÀI KHOẢN
    public boolean addAccount(Account acc) {
        String sql = "INSERT INTO ACCOUNT (full_name, email,username, password, role, status) VALUES (?, ?,?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, acc.getFullName());
            ps.setString(2, acc.getUsername());
            ps.setString(3, acc.getPassword());
            ps.setString(4, acc.getRole());
            ps.setString(5, acc.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // CẬP NHẬT TÀI KHOẢN
    public boolean updateAccount(Account acc) {String sql = "UPDATE ACCOUNT SET full_name = ?, password = ?, role = ?, status = ? WHERE username = ? AND id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, acc.getFullName());
            ps.setString(2, acc.getPassword());
            ps.setString(3, acc.getRole());
            ps.setString(4, acc.getStatus());
            ps.setString(5, acc.getUsername());
            ps.setInt(6, acc.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // XÓA TÀI KHOẢN
    public boolean deleteAccount(int id) {
        String sql = "DELETE FROM ACCOUNT WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false; // Lỗi nếu tài khoản đã có lịch sử bán vé (Vướng khóa ngoại)
        }
    }

    
    // 2.Tìm kiếm tài khoản dựa trên Email để phục vụ tính năng Quên mật khẩu 
    public Account findByEmail(String email) {
        String sql = "SELECT * FROM ACCOUNT WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Account acc = new Account();
                acc.setId(rs.getInt("id"));
                acc.setUsername(rs.getString("username"));
                acc.setPassword(rs.getString("password")); // Chuỗi băm BCrypt
                acc.setRole(rs.getString("role"));
                acc.setStatus(rs.getString("status"));
                acc.setFullName(rs.getString("full_name"));
                acc.setEmail(rs.getString("email"));
                return acc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updatePassword(int accountId, String hashedNewPassword) {
        String sql = "UPDATE ACCOUNT SET password = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hashedNewPassword);
            ps.setInt(2, accountId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đổi mật khẩu qua ID: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

     //2. Dành cho tính năng QUÊN MẬT KHẨU (Dựa vào Email đã xác thực)public boolean updatePasswordByEmail(String email, String hashedNewPassword) {
        String sql = "UPDATE ACCOUNT SET password = ? WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hashedNewPassword);
            ps.setString(2, email); 
            
            return ps.executeUpdate() > 0; 
            
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đặt lại mật khẩu qua Email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
