package com.spotify.spotifybackend.controller;

import com.spotify.spotifybackend.dto.SongDto;
import com.spotify.spotifybackend.model.Song;
import com.spotify.spotifybackend.service.SongService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSong(@Valid @RequestBody SongDto songDto) {
        try {
            Song newSong = songService.createSong(
                    songDto.getTitle(),
                    songDto.getDuration(),
                    songDto.getAlbumId()
            );
            return ResponseEntity.ok("Song created successfully with ID: " + newSong.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<java.util.List<Song>> getSongsByAlbum(@PathVariable Long albumId) {
        return ResponseEntity.ok(songService.getSongsByAlbum(albumId));
    }
}
