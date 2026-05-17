package com.cinema.dao;

import com.cinema.entity.Movie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {
	public Movie getMovieById(int id) {
        String sql = "SELECT * FROM MOVIE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Movie(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getInt("duration_minutes"),
                    rs.getString("genre"),
                    rs.getString("poster_url"),
                    rs.getString("description"),
                    rs.getDate("release_date"),
                    rs.getString("status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM MOVIE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                movies.add(new Movie(
                    rs.getInt("id"), rs.getString("title"), rs.getInt("duration_minutes"),
                    rs.getString("genre"), rs.getString("poster_url"), rs.getString("description"),
                    rs.getDate("release_date"), rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public boolean addMovie(Movie movie) {
        String sql = "INSERT INTO MOVIE (title, duration_minutes, genre, poster_url, description, release_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movie.getTitle());
            stmt.setInt(2, movie.getDurationMinutes());
            stmt.setString(3, movie.getGenre());
            stmt.setString(4, movie.getPosterUrl());
            stmt.setString(5, movie.getDescription());
            stmt.setDate(6, movie.getReleaseDate());
            stmt.setString(7, movie.getStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateMovie(Movie movie) {
        // Đã bổ sung description và poster_url vào lệnh UPDATE
        String sql = "UPDATE MOVIE SET title = ?, duration_minutes = ?, genre = ?, release_date = ?, status = ?, description = ?, poster_url = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, movie.getTitle());
            stmt.setInt(2, movie.getDurationMinutes());
            stmt.setString(3, movie.getGenre());
            stmt.setDate(4, new java.sql.Date(movie.getReleaseDate().getTime()));
            stmt.setString(5, movie.getStatus());
            stmt.setString(6, movie.getDescription()); 
            stmt.setString(7, movie.getPosterUrl());  
            stmt.setInt(8, movie.getId());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật phim: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


     //Xóa phim khỏi hệ thống dựa trên ID
    public boolean deleteMovie(int id) {
        String sql = "DELETE FROM MOVIE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
