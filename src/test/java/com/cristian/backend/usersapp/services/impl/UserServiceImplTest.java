//package com.cristian.backend.usersapp.services.impl;
//
//import com.cristian.backend.usersapp.exceptions.UserAlreadyExistsException;
//import com.cristian.backend.usersapp.models.dto.UserDto;
//import com.cristian.backend.usersapp.models.dto.mapper.DtoMapperUser;
//import com.cristian.backend.usersapp.models.entities.RoleEntity;
//import com.cristian.backend.usersapp.models.entities.UserEntity;
//import com.cristian.backend.usersapp.models.request.UserRequest;
//import com.cristian.backend.usersapp.repository.RoleRepository;
//import com.cristian.backend.usersapp.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.*;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class UserServiceImplTest {
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private RoleRepository roleRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    private UserEntity userEntity;
//    private RoleEntity roleEntity;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        roleEntity = new RoleEntity("ROLE_USER");
//        userEntity = new UserEntity();
//        userEntity.setId(1L);
//        userEntity.setUsername("john");
//        userEntity.setPassword("password");
//        userEntity.setEmail("john@example.com");
//        userEntity.setRoles(List.of(roleEntity));
//    }
//
//    @Test
//    void findAll() {
//        List<UserEntity> users = new ArrayList<>();
//        users.add(userEntity);
//
//        when(userRepository.findAll()).thenReturn(users);
//
//        List<UserDto> result = userService.findAll();
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.size());
//        assertEquals(userEntity.getUsername(), result.get(0).getUsername());
//    }
//
//    @Test
//    void findAll_withPagination() {
//        List<UserEntity> users = new ArrayList<>();
//        users.add(userEntity);
//        Page<UserEntity> page = new PageImpl<>(users);
//        Pageable pageable = PageRequest.of(0, 10);
//
//        when(userRepository.findAll(pageable)).thenReturn(page);
//
//        Page<UserDto> result = userService.findAll(pageable);
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.getTotalElements());
//        assertEquals(userEntity.getUsername(), result.getContent().get(0).getUsername());
//    }
//
//    @Test
//    void findById() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
//
//        Optional<UserDto> result = userService.findById(1L);
//        assertTrue(result.isPresent());
//        assertEquals(userEntity.getUsername(), result.get().getUsername());
//    }
//
//    @Test
//    void save() {
//        when(userRepository.existsByUsername(anyString())).thenReturn(false);
//        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(roleEntity));
//        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
//
//        UserDto result = userService.save(userEntity);
//        assertEquals(userEntity.getUsername(), result.getUsername());
//    }
//
//    @Test
//    void save_userAlreadyExists() {
//        when(userRepository.existsByUsername(anyString())).thenReturn(true);
//
//        assertThrows(UserAlreadyExistsException.class, () -> userService.save(userEntity));
//    }
//
//    @Test
//    void update() {
//        UserRequest userRequest = new UserRequest();
//        userRequest.setUsername("johnUpdated");
//        userRequest.setEmail("johnUpdated@example.com");
//        userRequest.setAdmin(true);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
//        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleEntity));
//        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new RoleEntity("ROLE_ADMIN")));
//        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
//
//        Optional<UserDto> result = userService.update(userRequest, 1L);
//        assertTrue(result.isPresent());
//        assertEquals(userRequest.getUsername(), result.get().getUsername());
//    }
//
//    @Test
//    void deleteById() {
//        userService.deleteById(1L);
//        verify(userRepository, times(1)).deleteById(1L);
//    }
//
//}