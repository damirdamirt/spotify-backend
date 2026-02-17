package com.spotify.spotifybackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Magic Numbers for JPEG (FF D8 FF) i PNG (89 50 4E 47)
    private static final byte[] JPEG_MAGIC = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC = new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47};

    public String saveFile(MultipartFile file, String expectedHash) throws IOException, NoSuchAlgorithmException {

        // 1. Validation: is it fie empty?
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot save empty file.");
        }

        // 2. Validation type (Whitelisting - Magic Numbers)
        if (!isValidImageFile(file)) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG and PNG are allowed (checked by magic numbers).");
        }

        // 3. Validation integrity (Hash check)
        String actualHash = calculateSha256(file);
        if (!actualHash.equalsIgnoreCase(expectedHash)) {
            // If hashes doesn't match, the file is corrupted or changed "in transit"
            throw new IllegalArgumentException("File integrity check failed! Hashes do not match.");
        }

        // 4. Saving file
        // Change the file name
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    //Help method for checking Magic Numbers
    private boolean isValidImageFile(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[4]; // Reading first four bytes
            if (is.read(header) < 4) {
                return false; // File is too short
            }

            // Check if it starts like JPEG (first 3 bytes)
            if (header[0] == JPEG_MAGIC[0] && header[1] == JPEG_MAGIC[1] && header[2] == JPEG_MAGIC[2]) {
                return true;
            }
            // Check if it starts like PNG (first 4 bytes)
            if (Arrays.equals(header, PNG_MAGIC)) {
                return true;
            }
        }
        return false;
    }

    public String calculateSha256(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        byte[] hashBytes = digest.digest();

        // Conversion of bytes to Hex String
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}