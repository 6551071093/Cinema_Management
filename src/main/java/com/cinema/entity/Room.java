package com.cinema.entity;

public class Room {
    private int id;
    private String name;
    private int numRows;
    private int numCols;
    private String status; // 'ACTIVE' or 'MAINTENANCE'

    public Room() {}

    public Room(int id, String name, int numRows, int numCols, String status) {
        this.id = id;
        this.name = name;
        this.numRows = numRows;
        this.numCols = numCols;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getNumRows() { return numRows; }
    public void setNumRows(int numRows) { this.numRows = numRows; }
    
    public int getNumCols() { return numCols; }
    public void setNumCols(int numCols) { this.numCols = numCols; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
