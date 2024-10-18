package com.cristian.backend.usersapp.models.dto.mapper;

import com.cristian.backend.usersapp.models.dto.UserDto;
import com.cristian.backend.usersapp.models.entities.UserEntity;
import lombok.Setter;

@Setter
public class DtoMapperUser {

    //patron builder

    private UserEntity user;

    private DtoMapperUser() {

    }

    public static DtoMapperUser builder() {
        return new DtoMapperUser();
    }

    public DtoMapperUser setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public UserDto build() {
        if (user == null) {
            throw new RuntimeException("must pass the entity user!");
        }
        boolean isAdmin = user.getRoles().stream().anyMatch( r -> "ROLE_ADMIN".equals(r.getName()) );
        return new UserDto(this.user.getId(), user.getUsername(), user.getEmail(), isAdmin);

    }
}
