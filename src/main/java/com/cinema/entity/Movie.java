package com.cinema.entity;

import java.sql.Date;

public class Movie {
    private int id;
    private String title;
    private int durationMinutes;
    private String genre;
    private String posterUrl;
    private String description;
    private Date releaseDate;
    private String status;

    public Movie() {}

    public Movie(int id, String title, int durationMinutes, String genre, String posterUrl, String description, Date releaseDate, String status) {
        this.id = id;
        this.title = title;
        this.durationMinutes = durationMinutes;
        this.genre = genre;
        this.posterUrl = posterUrl;
        this.description = description;
        this.releaseDate = releaseDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
