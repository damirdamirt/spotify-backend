package com.spotify.spotifybackend.repository;

import com.spotify.spotifybackend.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
