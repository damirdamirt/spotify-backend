package com.spotify.spotifybackend.service;

import com.spotify.spotifybackend.model.Album;
import com.spotify.spotifybackend.model.Artist;
import com.spotify.spotifybackend.repository.AlbumRepository;
import com.spotify.spotifybackend.repository.ArtistRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final FileService fileService; // Koristimo tvoj postojeci servis za fajlove!

    public AlbumService(AlbumRepository albumRepository, ArtistRepository artistRepository, FileService fileService) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.fileService = fileService;
    }

    // Metoda za kreiranje albuma sa slikom
    public Album createAlbum(String title, Integer releaseYear, Long artistId,
                             MultipartFile coverImage, String fileHash) throws IOException,
                                    NoSuchAlgorithmException {


        // 2. Sacuvaj sliku (koristeci tvoj Secure Upload logic iz FileService-a)
        String imagePath = fileService.saveFile(coverImage, fileHash); // Ovo vraca putanju gde je slika sacuvana


        // 1. Nadji umetnika (jer album mora pripadati nekome)
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException("Artist not found with id: " + artistId));

        // 3. Napravi Album objekat
        Album album = new Album();
        album.setTitle(title);
        album.setReleaseYear(releaseYear);
        album.setArtist(artist);
        album.setCoverImage(imagePath); // Upisujemo putanju slike u bazu

        // 4. Sacuvaj u bazu
        return albumRepository.save(album);
    }

    // Metoda za citanje svih albuma (trebace nam za prikaz)
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    // Metoda za citanje albuma jednog umetnika
    public List<Album> getAlbumsByArtist(Long artistId) {
        return albumRepository.findByArtistId(artistId);
    }
}
