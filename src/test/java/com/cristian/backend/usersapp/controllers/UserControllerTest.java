package com.cristian.backend.usersapp.controllers;

import com.cristian.backend.usersapp.controllers.UserController;
import com.cristian.backend.usersapp.exceptions.UserAlreadyExistsException;
import com.cristian.backend.usersapp.models.dto.UserDto;
import com.cristian.backend.usersapp.models.entities.RoleEntity;
import com.cristian.backend.usersapp.models.entities.UserEntity;
import com.cristian.backend.usersapp.models.request.UserRequest;
import com.cristian.backend.usersapp.repository.RoleRepository;
import com.cristian.backend.usersapp.repository.UserRepository;
import com.cristian.backend.usersapp.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@ExtendWith({SpringExtension.class, MockitoExtension.class})
//@WebMvcTest(UserController.class)
//@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    private UserEntity user;

    private RoleEntity role;

    @BeforeEach
    public void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("testpassword"));
        user.setEmail("testuser@example.com");

        role = new RoleEntity();
        role.setId(1L);
        role.setName("ROLE_ADMIN");
        roleRepository.save(role);

        user.setRoles(Collections.singletonList(role));
        userRepository.save(user);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void testListUsers() {
        List<UserDto> users = Collections.singletonList(new UserDto(user.getId(), user.getUsername(), user.getEmail(), false));
        when(userService.findAll()).thenReturn(users);

        ResponseEntity<List<UserDto>> response = restTemplate.exchange("/users", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<UserDto>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    public void testGetUserById() {
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), false);
        when(userService.findById(user.getId())).thenReturn(Optional.of(userDto));

        ResponseEntity<UserDto> response = restTemplate.getForEntity("/users/{id}", UserDto.class, user.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getUsername(), response.getBody().getUsername());
    }

    @Test
    public void testCreateUser() {
        UserEntity newUser = new UserEntity();
        newUser.setUsername("newuser");
        newUser.setPassword(passwordEncoder.encode("newpassword"));
        newUser.setEmail("newuser@example.com");

        UserDto newUserDto = new UserDto(2L, newUser.getUsername(), newUser.getEmail(), false);
        when(userService.save(any(UserEntity.class))).thenReturn(newUserDto);

        HttpEntity<UserEntity> request = new HttpEntity<>(newUser);
        ResponseEntity<UserDto> response = restTemplate.postForEntity("/users", request, UserDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newUser.getUsername(), response.getBody().getUsername());
    }

    @Test
    public void testUpdateUser() {
        UserRequest updatedUser = new UserRequest();
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updateduser@example.com");

        UserDto updatedUserDto = new UserDto(user.getId(), updatedUser.getUsername(), updatedUser.getEmail(), false);
        when(userService.update(any(UserRequest.class), eq(user.getId()))).thenReturn(Optional.of(updatedUserDto));

        HttpEntity<UserRequest> request = new HttpEntity<>(updatedUser);
        ResponseEntity<UserDto> response = restTemplate.exchange("/users/{id}", HttpMethod.PUT, request,
                UserDto.class, user.getId());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedUser.getUsername(), response.getBody().getUsername());
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userService).deleteById(user.getId());

        ResponseEntity<Void> response = restTemplate.exchange("/users/{id}", HttpMethod.DELETE, null,
                Void.class, user.getId());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(userRepository.findById(user.getId()).isPresent());
    }
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserService userService;
//
//    private UserEntity user;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private String authToken;
//
//    @BeforeEach
//    public void setup() {
//        // Simular la generación de un token JWT válido
//        authToken = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyX2RldmVsb3BlciIsImV4cCI6MTYyNTY4NDM4OH0.V0VHd3"; // Aquí debes incluir tu token JWT válido
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"ADMIN"})
//    public void testListUsers() throws Exception {
//        // Mock data
//        UserDto userDto = new UserDto(1L, "testuser", "test@example.com", true);
//        List<UserDto> userList = Collections.singletonList(userDto);
//        when(userService.findAll()).thenReturn(userList);
//
//        // Perform GET request with Authorization header containing JWT token
//        mockMvc.perform(MockMvcRequestBuilders.get("/users")
//                        .header(HttpHeaders.AUTHORIZATION, authToken)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("testuser"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("test@example.com"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].admin").value(true));
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"ADMIN"})
//    public void testListUsersPage() throws Exception {
//        // Mock data para la paginación
//        UserDto userDto = new UserDto(1L, "testuser", "test@example.com", true);
//        List<UserDto> userList = Collections.singletonList(userDto);
//        PageImpl<UserDto> page = new PageImpl<>(userList, PageRequest.of(0, 4), userList.size());
//        when(userService.findAll(PageRequest.of(0, 4))).thenReturn(page);
//
//        // Realizar solicitud GET con header Authorization que contiene el token JWT
//        mockMvc.perform(MockMvcRequestBuilders.get("/users/page/0")
//                        .header(HttpHeaders.AUTHORIZATION, authToken)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].username").value("testuser"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].email").value("test@example.com"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].admin").value(true));
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"ADMIN"})
//    public void testShowUserById() throws Exception {
//        // Mock data para encontrar usuario por ID
//        Long userId = 1L;
//        UserDto userDto = new UserDto(userId, "testuser", "test@example.com", true);
//        when(userService.findById(userId)).thenReturn(Optional.of(userDto));
//
//        // Realizar solicitud GET para obtener usuario por ID
//        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + userId)
//                        .header(HttpHeaders.AUTHORIZATION, authToken)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testuser"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.admin").value(true));
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"ADMIN"})
//    public void testShowUserByIdNotFound() throws Exception {
//        // Mock data para encontrar usuario por ID
//        Long userId = 1L;
//        when(userService.findById(userId)).thenReturn(Optional.empty());
//
//        // Realizar solicitud GET para obtener usuario por ID
//        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + userId)
//                        .header(HttpHeaders.AUTHORIZATION, authToken)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"ADMIN"})
//    public void testCreateUser() throws Exception {
//        // Mock data para crear un usuario
//        UserDto userDto = new UserDto(1L, "testuser", "test@example.com", true);
//
//        // Realizar solicitud POST para crear un usuario
//        mockMvc.perform(MockMvcRequestBuilders.post("/users")
//                        .header(HttpHeaders.AUTHORIZATION, authToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(userDto)))
//                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testuser"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.admin").value(true));
//    }
//
//
//
//    private String asJsonString(final Object obj) {
//        try {
//            return new ObjectMapper().writeValueAsString(obj);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}