package com.spotify.spotifybackend.dto;

public class MagicLinkLoginDto {

    private String token;

    public MagicLinkLoginDto() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
