package com.spotify.spotifybackend.service;

import com.spotify.spotifybackend.dto.*;

public interface AuthService {
    UserDto register(UserRegistrationDto registrationDto);
    void verifyUser(String token);
    void login(LoginRequestDto loginRequest);
    AuthResponseDto verifyOtp(VerifyOtpDto input);
    void sendMagicLoginLink(MagicLinkRequestDto input);
    AuthResponseDto loginWithMagicLink(MagicLinkLoginDto input);
    void initiatePasswordChange(InitiatePasswordChangeDto input);
    void finalizePasswordChange(FinishPasswordChangeDto input);
}
