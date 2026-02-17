package com.spotify.spotifybackend.repository;

import com.spotify.spotifybackend.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    // Trebace nam da nadjemo sve pesme sa albuma
    List<Song> findByAlbumId(Long albumId);
}