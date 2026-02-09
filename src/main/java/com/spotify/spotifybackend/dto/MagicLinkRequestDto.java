package com.spotify.spotifybackend.dto;

public class MagicLinkRequestDto {

    private String email;

    public MagicLinkRequestDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
