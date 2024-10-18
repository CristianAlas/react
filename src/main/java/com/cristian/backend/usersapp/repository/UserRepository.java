package com.cristian.backend.usersapp.repository;

import com.cristian.backend.usersapp.models.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u WHERE u.username=?1")
    Optional<UserEntity> findByUsername(String username);

    Page<UserEntity> findAll(Pageable pageable);

    boolean existsByUsername(String username);
}
