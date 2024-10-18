package com.cristian.backend.usersapp.repository;

import com.cristian.backend.usersapp.models.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    @Query("SELECT r FROM RoleEntity r WHERE r.name=?1")
    Optional<RoleEntity> findByName(String name);
}
