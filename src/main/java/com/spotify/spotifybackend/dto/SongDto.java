package com.spotify.spotifybackend.dto;

import jakarta.validation.constraints.*;

public class SongDto {

    // 1. Whitelisting (Samo slova, brojevi i razmaci) - XSS Zastita
    @NotBlank(message = "Title is required")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Title contains invalid characters.")
    @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
    private String title;

    // 2. Trajanje u sekundama (npr. 180 sekundi = 3 minuta)
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be positive")
    @Max(value = 1200, message = "Song is too long (max 20 mins)")
    private Integer duration;

    // 3. Veza sa albumom (mora postojati)
    @NotNull(message = "Album ID is required")
    private Long albumId;

    public SongDto() {}

    public SongDto(String title, Integer duration, Long albumId) {
        this.title = title;
        this.duration = duration;
        this.albumId = albumId;
    }

    // Getteri i Setteri
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public Long getAlbumId() { return albumId; }
    public void setAlbumId(Long albumId) { this.albumId = albumId; }
}
