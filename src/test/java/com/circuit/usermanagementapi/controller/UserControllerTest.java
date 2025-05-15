package com.circuit.usermanagementapi.controller;

import com.circuit.usermanagementapi.model.ERole;
import com.circuit.usermanagementapi.model.Role;
import com.circuit.usermanagementapi.model.Team;
import com.circuit.usermanagementapi.model.User;
import com.circuit.usermanagementapi.payload.response.MessageResponse;
import com.circuit.usermanagementapi.repository.TeamRepository;
import com.circuit.usermanagementapi.repository.UserRepository;
import com.circuit.usermanagementapi.security.UserSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserSecurity userSecurity;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    private User user;
    private Team team;
    private final String USERNAME = "testuser";
    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "password123";
    private final Long USER_ID = 1L;
    private final Long TEAM_ID = 1L;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        // Create team
        team = new Team("Test Team", "Test Team Description");
        team.setId(TEAM_ID);

        // Create user
        user = new User(USERNAME, EMAIL, PASSWORD);
        user.setId(USER_ID);
        user.setFirstName("Test");
        user.setLastName("User");

        // Set up roles
        Set<Role> roles = new HashSet<>();
        Role role = new Role(ERole.ROLE_TEAM_MEMBER);
        role.setId(1L);
        roles.add(role);
        user.setRoles(roles);

        // Set up preferences
        Map<String, String> preferences = new HashMap<>();
        preferences.put("theme", "dark");
        user.setPreferences(preferences);
    }

    @Test
    void testGetAllUsers() throws Exception {
        // Mock repository
        when(userRepository.findAll()).thenReturn(List.of(user));

        // Perform request
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(USER_ID))
                .andExpect(jsonPath("$[0].username").value(USERNAME))
                .andExpect(jsonPath("$[0].email").value(EMAIL));
    }

    @Test
    void testGetUserById_Success() throws Exception {
        // Mock repository
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Perform request
        mockMvc.perform(get("/api/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        // Mock repository
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Perform request
        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCurrentUser_Success() throws Exception {
        // Mock SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(USERNAME);

        // Mock repository
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        // Perform request
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    void testGetCurrentUser_NotFound() throws Exception {
        // Mock SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("nonexistentuser");

        // Mock repository
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // Perform request
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUserPreferences_Success() throws Exception {
        // Mock repository
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Create preferences
        Map<String, String> newPreferences = new HashMap<>();
        newPreferences.put("language", "en");

        // Perform request
        mockMvc.perform(put("/api/users/{id}/preferences", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPreferences)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User preferences updated successfully!"));

        // Verify that the user was saved with updated preferences
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserPreferences_UserNotFound() throws Exception {
        // Mock repository
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Create preferences
        Map<String, String> newPreferences = new HashMap<>();
        newPreferences.put("language", "en");

        // Perform request
        mockMvc.perform(put("/api/users/{id}/preferences", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPreferences)))
                .andExpect(status().isNotFound());

        // Verify that no user was saved
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Mock repository
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Perform request
        mockMvc.perform(delete("/api/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully!"));

        // Verify that the user was deleted
        verify(userRepository).delete(user);
    }

    @Test
    void testDeleteUser_UserNotFound() throws Exception {
        // Mock repository
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Perform request
        mockMvc.perform(delete("/api/users/{id}", 999L))
                .andExpect(status().isNotFound());

        // Verify that no user was deleted
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testUpdateUserTeam_Success() throws Exception {
        // Mock repositories
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Create request body
        UserController.UpdateTeamRequest updateTeamRequest = new UserController.UpdateTeamRequest();
        updateTeamRequest.setTeamId(TEAM_ID);

        // Perform request
        mockMvc.perform(put("/api/users/{id}/team", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTeamRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User team updated successfully!"));

        // Verify that the user was saved with the new team
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserTeam_RemoveFromTeam() throws Exception {
        // Set up user with a team
        user.setTeam(team);

        // Mock repository
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Create request body with null teamId
        UserController.UpdateTeamRequest updateTeamRequest = new UserController.UpdateTeamRequest();
        updateTeamRequest.setTeamId(null);

        // Perform request
        mockMvc.perform(put("/api/users/{id}/team", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTeamRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User removed from team successfully!"));

        // Verify that the user was saved with no team
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserTeam_UserNotFound() throws Exception {
        // Mock repository
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Create request body
        UserController.UpdateTeamRequest updateTeamRequest = new UserController.UpdateTeamRequest();
        updateTeamRequest.setTeamId(TEAM_ID);

        // Perform request
        mockMvc.perform(put("/api/users/{id}/team", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTeamRequest)))
                .andExpect(status().isNotFound());

        // Verify that no user was saved
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserTeam_TeamNotFound() throws Exception {
        // Mock repositories
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Create request body
        UserController.UpdateTeamRequest updateTeamRequest = new UserController.UpdateTeamRequest();
        updateTeamRequest.setTeamId(999L);

        // Perform request
        mockMvc.perform(put("/api/users/{id}/team", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTeamRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Team not found!"));

        // Verify that no user was saved
        verify(userRepository, never()).save(any(User.class));
    }
}
