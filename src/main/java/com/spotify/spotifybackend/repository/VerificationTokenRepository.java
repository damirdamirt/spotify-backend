package com.spotify.spotifybackend.repository;

import com.spotify.spotifybackend.model.User;
import com.spotify.spotifybackend.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);
    VerificationToken findByUser(User user);
}
