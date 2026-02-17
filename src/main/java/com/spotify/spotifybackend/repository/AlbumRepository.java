package com.spotify.spotifybackend.repository;

import com.spotify.spotifybackend.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    // Trebace nam da nadjemo sve albume odredjenog umetnika
    List<Album> findByArtistId(Long artistId);
}
