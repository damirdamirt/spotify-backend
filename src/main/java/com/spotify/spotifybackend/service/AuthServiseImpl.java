package com.spotify.spotifybackend.service;

import com.spotify.spotifybackend.converter.UserRegistrationDtoToUserConverter;
import com.spotify.spotifybackend.converter.UserToUserDtoConverter;
import com.spotify.spotifybackend.dto.*;
import com.spotify.spotifybackend.model.Authority;
import com.spotify.spotifybackend.model.User;
import com.spotify.spotifybackend.model.VerificationToken;
import com.spotify.spotifybackend.repository.AuthorityRepository;
import com.spotify.spotifybackend.repository.UserRepository;
import com.spotify.spotifybackend.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
public class AuthServiseImpl implements AuthService{

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRegistrationDtoToUserConverter toUserConverter;
    private final UserToUserDtoConverter toDtoConverter;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;

    public AuthServiseImpl(UserRepository userRepository,
                           AuthorityRepository authorityRepository,
                           PasswordEncoder passwordEncoder,
                           UserRegistrationDtoToUserConverter toUserConverter,
                           UserToUserDtoConverter toDtoConverter,
                           VerificationTokenRepository verificationTokenRepository,
                           EmailService emailService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.toUserConverter = toUserConverter;
        this.toDtoConverter = toDtoConverter;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    @Override
    public UserDto register(UserRegistrationDto registrationDto) {
        if(!registrationDto.getPassword().equals(registrationDto.getRepeatedPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if(userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if(userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = toUserConverter.convert(registrationDto);

        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setLastPasswordResetDate(LocalDateTime.now());

        Authority userRole = authorityRepository.findByName("ROLE_USER");
        if(userRole == null) {
            userRole = new Authority();
            userRole.setName("ROLE_USER");
            authorityRepository.save(userRole);
        }

        Set<Authority> authorities = new HashSet<>();
        authorities.add(userRole);
        user.setAuthorities(authorities);

        User savedUser = userRepository.save(user);

        VerificationToken verificationToken = new VerificationToken(savedUser);
        verificationTokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken.getToken());

        return toDtoConverter.convert(savedUser);
    }

    @Override
    public void verifyUser(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if(verificationToken == null) {
            throw new IllegalArgumentException("Invalid token for registration");
        }

        if(verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw  new IllegalArgumentException("Token iz expiried, please register again");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }

    @Override
    public void login(LoginRequestDto loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User with that username doesn't exist"));

        if(user.getLockOutEndTime() != null && user.getLockOutEndTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Account is temporary locked due to expired password. " +
                                               "Wait for 2 minutes before next try");
        }

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw  new IllegalArgumentException("Wrong password");
        }

        if(user.getLastPasswordResetDate() != null &&
                user.getLastPasswordResetDate().isBefore(LocalDateTime.now().minusDays(60))) {
            throw new IllegalArgumentException("Your password is expired (older then 60 days). " +
                                               "Your account has been locked for 2 minutes. " +
                                               "Please, after that reset the password");
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setOtpCode(otp);
        user.setOtpExpirationTime(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    public AuthResponseDto verifyOtp(VerifyOtpDto input) {

        User user = userRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User doesn't exist"));

        if (user.getOtpCode() == null || !user.getOtpCode().equals(input.getOtp())) {
            throw new IllegalArgumentException("Otp code is invalid");
        }

        if(user.getOtpExpirationTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Otp code is expired, please login again");
        }

        user.setOtpCode(null);
        user.setOtpExpirationTime(null);

        user.setLockOutEndTime(null);

        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());

        return new AuthResponseDto(token);
    }


}
