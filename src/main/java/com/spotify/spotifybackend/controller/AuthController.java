package com.spotify.spotifybackend.controller;

import com.spotify.spotifybackend.dto.*;
import com.spotify.spotifybackend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        UserDto result = authService.register(registrationDto);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        authService.verifyUser(token);
        return ResponseEntity.ok("Your account has been successfully registered");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequest) {
        authService.login(loginRequest);
        return  ResponseEntity.ok("OTP code is sent on your email. Enter it to log in");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponseDto> verifyOtp(@RequestBody VerifyOtpDto verifyOtpDto) {
        AuthResponseDto response = authService.verifyOtp(verifyOtpDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/magic-link/request")
    public ResponseEntity<String> requestMagicLink(@RequestBody MagicLinkRequestDto dto) {
        authService.sendMagicLoginLink(dto);
        return ResponseEntity.ok("Magic link has sent on your email.");
    }

    @PostMapping("/magic-link/login")
    public ResponseEntity<AuthResponseDto> loginMagicLink(@RequestBody MagicLinkLoginDto dto) {
        return ResponseEntity.ok(authService.loginWithMagicLink(dto));
    }

    @PostMapping("/password-change/initiate")
    public ResponseEntity<String> initiatePasswordChange(@RequestBody InitiatePasswordChangeDto dto) {
        authService.initiatePasswordChange(dto);
        return ResponseEntity.ok("Link with change password has sent on your email.");
    }

    @PostMapping("/password-change/finalize")
    public ResponseEntity<String> finalizePasswordChange(@Valid @RequestBody FinishPasswordChangeDto dto) {
        authService.finalizePasswordChange(dto);
        return ResponseEntity.ok("Password changed successfully. You can login.");
    }
}
