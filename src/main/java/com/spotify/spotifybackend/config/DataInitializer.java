package com.spotify.spotifybackend.config;

import com.spotify.spotifybackend.model.Artist;
import com.spotify.spotifybackend.model.Authority;
import com.spotify.spotifybackend.model.User;
import com.spotify.spotifybackend.repository.ArtistRepository;
import com.spotify.spotifybackend.repository.AuthorityRepository;
import com.spotify.spotifybackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ArtistRepository artistRepository;

    public DataInitializer(AuthorityRepository authorityRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder, ArtistRepository artistRepository) {
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.artistRepository = artistRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Inicijalizuj Role (Ako ne postoje)
        Authority userRole = createAuthorityIfNotFound("ROLE_USER");
        Authority adminRole = createAuthorityIfNotFound("ROLE_ADMIN");

        // 2. Kreiraj Default Admina (da imaš čime da testiraš Upload!)
        // Proveravamo da li postoji, da ga ne bi pravili svaki put kad pokreneš aplikaciju
        if (userRepository.findByEmail("admin@spotify.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@spotify.com");
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setPassword(passwordEncoder.encode("Admin123!")); // Šifra za admina
            admin.setEnabled(true); // Admin je odmah aktivan (ne mora da verifikuje mail)
            admin.setAge(30); // Zbog one validacije godina

            Set<Authority> authorities = new HashSet<>();
            authorities.add(userRole);
            authorities.add(adminRole); // Admin ima OBE role (može da radi i sve što i običan user)
            admin.setAuthorities(authorities);

            userRepository.save(admin);
            System.out.println("✅ ADMIN KORISNIK KREIRAN: email=admin@spotify.com, pass=Admin123!");
        }

        if (artistRepository.count() == 0) {
            createArtist("Sergej Ćetković", "Popular pop singer and songwriter known for romantic ballads.");
            createArtist("Adele", "British soul and pop diva with an incredible voice.");
            createArtist("Kerber", "Legendary hard rock band from Niš.");
            createArtist("Massimo Savić", "Musician with a unique vocal and artistic style.");

            System.out.println("✅ Artists successfully added to the database!");
        }
    }

    private Authority createAuthorityIfNotFound(String name) {
        Authority authority = authorityRepository.findByName(name);
        if (authority == null) {
            authority = new Authority();
            authority.setName(name);
            authorityRepository.save(authority);
            System.out.println("✅ KREIRANA ROLA: " + name);
        }
        return authority;
    }

    private void createArtist(String name, String description) {
        Artist artist = new Artist();
        artist.setName(name);
        artist.setDescription(description);
        artistRepository.save(artist);
    }
}
