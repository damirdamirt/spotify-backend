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
}
