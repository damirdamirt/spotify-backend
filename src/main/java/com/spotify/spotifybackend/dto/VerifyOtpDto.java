package com.spotify.spotifybackend.dto;

public class VerifyOtpDto {

    private String username;
    private  String otp;

    public VerifyOtpDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
