package com.spotify.spotifybackend.converter;

import com.spotify.spotifybackend.dto.UserDto;
import com.spotify.spotifybackend.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements Converter<User, UserDto> {

    @Override
    public UserDto convert(User source) {
        if (source == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(source.getId());
        dto.setUsername(source.getUsername());
        dto.setFirstName(source.getFirstName());
        dto.setLastName(source.getLastName());
        dto.setEmail(source.getEmail());

        return dto;
    }
}
