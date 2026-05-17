package com.cinema.entity;

public class Account {
    private int id;
    private String fullName;
    private String email;
    private String username;
    private String password;
    private String role;
    private String status;

    public Account() {
    }

    public Account(int id, String fullName, String email, String username, String password, String role, String status) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public Account(String fullName, String email, String username, String role) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
}
