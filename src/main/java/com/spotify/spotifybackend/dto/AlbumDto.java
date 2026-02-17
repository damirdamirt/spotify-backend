package com.spotify.spotifybackend.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public class AlbumDto {

    // 1. WHITELISTING & SPECIAL CHARACTERS CHECK & CHARACTER ESCAPING (Preventiva)
    // Dozvoljavamo samo slova (a-z, A-Z), brojeve (0-9) i razmak.
    // Sve ostalo (ukljucujuci <, >, &, ", ') je ZABRANJENO.
    // Ovo automatski resava XSS jer tagovi ne mogu da prodju.
    @NotBlank(message = "Title is required")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Title contains invalid characters. Only letters, numbers and spaces are allowed.")

    // 2. BOUNDARY CHECKING (String)
    // Proveravamo duzinu inputa (min i max granice)
    @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
    private String title;

    // 3. NUMERIC VALIDATION & BOUNDARY CHECKING (Integer)
    // Proveravamo da li je broj i da li je u realnim granicama
    @NotNull(message = "Release year is required")
    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2026, message = "Year cannot be in the future")
    private Integer releaseYear;

    @NotNull(message = "Artist ID is required")
    private Long artistId;

    @NotNull(message = "Cover image is required")
    private MultipartFile coverImage;

    @NotBlank(message = "File hash is required for integrity check")
    private String fileHash;

    public AlbumDto() {
    }

    public AlbumDto(String title, Integer releaseYear, Long artistId, MultipartFile coverImage, String fileHash) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.artistId = artistId;
        this.coverImage = coverImage;
        this.fileHash = fileHash;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public MultipartFile getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(MultipartFile coverImage) {
        this.coverImage = coverImage;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
}
