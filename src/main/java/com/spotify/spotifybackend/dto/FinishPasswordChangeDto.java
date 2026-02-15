package com.spotify.spotifybackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class FinishPasswordChangeDto {

    private String token;
    private String oldPassword;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one digit, " +
                    "one uppercase letter, one lowercase letter, and one special character")
    @Size(max = 100, message = "Password cannot be longer than 100 characters")
    private String newPassword;

    @NotBlank(message = "Repeated password is required")
    @Size(max = 100, message = "Password cannot be longer than 100 characters")
    private String confirmPassword;

    public FinishPasswordChangeDto() {
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
