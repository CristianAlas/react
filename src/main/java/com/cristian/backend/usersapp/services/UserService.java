package com.cristian.backend.usersapp.services;

import com.cristian.backend.usersapp.models.dto.UserDto;
import com.cristian.backend.usersapp.models.entities.UserEntity;
import com.cristian.backend.usersapp.models.request.UserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> findAll();
    Page<UserDto> findAll(Pageable pageable);
    Optional<UserDto> findById(Long id);
    UserDto save(UserEntity user);
    Optional<UserDto> update(UserRequest user, Long id);
    void deleteById(Long id);

    //Optional<UserEntity> findByUsername(String username);
}
