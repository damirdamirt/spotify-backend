package com.spotify.spotifybackend.controller;

import com.spotify.spotifybackend.dto.AlbumDto;
import com.spotify.spotifybackend.model.Album;
import com.spotify.spotifybackend.service.AlbumService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    // CREATE - Validacija ulaznih podataka
    @PostMapping("/create")
    public ResponseEntity<?> createAlbum(@Valid @ModelAttribute AlbumDto albumDto) {

        // Ako @Valid prodje, znaci da nema specijalnih karaktera (XSS sigurno),
        // duzine su dobre i brojevi su validni.

        try {
            // Pozivamo servis sa validiranim podacima iz DTO-a
            Album newAlbum = albumService.createAlbum(
                    albumDto.getTitle(),
                    albumDto.getReleaseYear(),
                    albumDto.getArtistId(),
                    albumDto.getCoverImage(),
                    albumDto.getFileHash()
            );
            return ResponseEntity.ok("Album created successfully with ID: " + newAlbum.getId());

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // READ - Svi albumi odredjenog umetnika (Za tacku 1.6)
    // GET /api/albums/artist/{artistId}
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Album>> getAlbumsByArtist(@PathVariable Long artistId) {
        List<Album> albums = albumService.getAlbumsByArtist(artistId);
        return ResponseEntity.ok(albums);
    }

    // READ - Svi albumi (ako zatreba)
    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }
}
