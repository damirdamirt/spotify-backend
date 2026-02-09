package com.spotify.spotifybackend.dto;

public class InitiatePasswordChangeDto {

        private String username;

        public InitiatePasswordChangeDto() {

        }

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }

}
