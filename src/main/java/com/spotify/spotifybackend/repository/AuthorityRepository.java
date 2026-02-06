package com.spotify.spotifybackend.repository;

import com.spotify.spotifybackend.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findByName(String name);
}
