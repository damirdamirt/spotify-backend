package com.spotify.spotifybackend.converter;

import com.spotify.spotifybackend.dto.UserRegistrationDto;
import com.spotify.spotifybackend.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationDtoToUserConverter implements Converter<UserRegistrationDto, User> {


    @Override
    public User convert(UserRegistrationDto source) {
        if(source == null) {
            return null;
        }

        User user = new User();
        user.setUsername(source.getUsername());
        user.setFirstName(source.getFirstName());
        user.setLastName(source.getLastName());
        user.setEmail(source.getEmail());
        user.setPassword(source.getPassword());
        user.setEnabled(false);

        return user;
    }
}
