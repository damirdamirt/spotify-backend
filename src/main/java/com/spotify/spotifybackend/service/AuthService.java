package com.spotify.spotifybackend.service;

import com.spotify.spotifybackend.dto.UserDto;
import com.spotify.spotifybackend.dto.UserRegistrationDto;

public interface AuthService {
    UserDto register(UserRegistrationDto registrationDto);
    void verifyUser(String token);
}
