package com.cristian.backend.usersapp.services.impl;

import com.cristian.backend.usersapp.exceptions.UserAlreadyExistsException;
import com.cristian.backend.usersapp.models.IUser;
import com.cristian.backend.usersapp.models.dto.UserDto;
import com.cristian.backend.usersapp.models.dto.mapper.DtoMapperUser;
import com.cristian.backend.usersapp.models.entities.RoleEntity;
import com.cristian.backend.usersapp.models.entities.UserEntity;
import com.cristian.backend.usersapp.models.request.UserRequest;
import com.cristian.backend.usersapp.repository.RoleRepository;
import com.cristian.backend.usersapp.repository.UserRepository;
import com.cristian.backend.usersapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<UserEntity> users = (List<UserEntity>) repository.findAll();

        return users
                .stream()
                .map(u -> DtoMapperUser.builder().setUser(u).build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> findAll(Pageable pageable) {
        Page<UserEntity> usersPage = repository.findAll(pageable);
        return usersPage.map(u -> DtoMapperUser.builder().setUser(u).build());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
//        return repository.findById(id).map(u -> DtoMapperUser
//                .builder()
//                .setUser(u)
//                .build());
        return repository.findById(id).map(u -> DtoMapperUser.builder().setUser(u).build());
    }

    @Override
    @Transactional
    public UserDto save(UserEntity user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("El usuario ya existe");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(getRoles(user));
        return DtoMapperUser.builder().setUser(repository.save(user)).build();
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setRoles(getRoles(user));
//        return DtoMapperUser.builder().setUser(repository.save(user)).build();
    }

    @Override
    @Transactional
    public Optional<UserDto> update(UserRequest user, Long id) {
        Optional<UserEntity> userOptional = repository.findById(id);
        UserEntity uOptional = null;
        if(userOptional.isPresent()){
            UserEntity userEntity = userOptional.orElseThrow();
            userEntity.setRoles(getRoles(user));
            userEntity.setUsername(user.getUsername());
            userEntity.setEmail(user.getEmail());
            uOptional = repository.save(userEntity);
        }
        return Optional.ofNullable(DtoMapperUser.builder().setUser(uOptional).build());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private List<RoleEntity> getRoles(IUser user) {
        Optional<RoleEntity> ou = roleRepository.findByName("ROLE_USER");

        List<RoleEntity> roles = new ArrayList<>();
        if (ou.isPresent()) {
            roles.add(ou.orElseThrow());
        }

        if(user.isAdmin()) {
            Optional<RoleEntity> oa = roleRepository.findByName("ROLE_ADMIN");
            if(oa.isPresent()) {
                roles.add(oa.orElseThrow());
            }
        }
        return roles;
    }
}
