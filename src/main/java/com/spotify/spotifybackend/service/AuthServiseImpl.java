package com.spotify.spotifybackend.service;

import com.spotify.spotifybackend.converter.UserRegistrationDtoToUserConverter;
import com.spotify.spotifybackend.converter.UserToUserDtoConverter;
import com.spotify.spotifybackend.dto.UserDto;
import com.spotify.spotifybackend.dto.UserRegistrationDto;
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

    public AuthServiseImpl(UserRepository userRepository,
                           AuthorityRepository authorityRepository,
                           PasswordEncoder passwordEncoder,
                           UserRegistrationDtoToUserConverter toUserConverter,
                           UserToUserDtoConverter toDtoConverter,
                           VerificationTokenRepository verificationTokenRepository,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.toUserConverter = toUserConverter;
        this.toDtoConverter = toDtoConverter;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
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
}
