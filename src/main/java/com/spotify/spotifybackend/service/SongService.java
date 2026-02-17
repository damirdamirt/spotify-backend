package com.spotify.spotifybackend.service;

import com.spotify.spotifybackend.model.Album;
import com.spotify.spotifybackend.model.Song;
import com.spotify.spotifybackend.repository.AlbumRepository;
import com.spotify.spotifybackend.repository.SongRepository;
import org.springframework.stereotype.Service;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;

    public SongService(SongRepository songRepository, AlbumRepository albumRepository) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
    }

    public Song createSong(String title, Integer duration, Long albumId) {
        // 1. Provera da li album postoji (Zahtev zadatka)
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album not found with id: " + albumId));

        // 2. Kreiranje pesme
        Song song = new Song();
        song.setTitle(title);

        String formattedDuration = formatDuration(duration);
        song.setDuration(formattedDuration);

        song.setAlbum(album); // Povezivanje (Foreign Key)

        return songRepository.save(song);
    }

    // Pomocna metoda za formatiranje (Mozes je staviti private dole)
    private String formatDuration(Integer totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds); // Rezultat npr. "3:05"
    }

    public java.util.List<Song> getSongsByAlbum(Long albumId) {
        return songRepository.findByAlbumId(albumId);
    }
}
