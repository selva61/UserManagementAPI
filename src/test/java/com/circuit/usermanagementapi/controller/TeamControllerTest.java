package com.circuit.usermanagementapi.controller;

import com.circuit.usermanagementapi.model.Team;
import com.circuit.usermanagementapi.model.User;
import com.circuit.usermanagementapi.payload.request.TeamRequest;
import com.circuit.usermanagementapi.repository.TeamRepository;
import com.circuit.usermanagementapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TeamController teamController;

    private Team team;
    private User user;
    private final Long TEAM_ID = 1L;
    private final Long USER_ID = 1L;
    private final String TEAM_NAME = "Test Team";
    private final String TEAM_DESCRIPTION = "Test Team Description";

    @BeforeEach
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();

        // Create team
        team = new Team(TEAM_NAME, TEAM_DESCRIPTION);
        team.setId(TEAM_ID);

        // Create user
        user = new User("testuser", "test@example.com", "password123");
        user.setId(USER_ID);
    }

    @Test
    void testGetAllTeams() throws Exception {
        // Mock repository
        when(teamRepository.findAll()).thenReturn(List.of(team));

        // Perform request
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEAM_ID))
                .andExpect(jsonPath("$[0].name").value(TEAM_NAME))
                .andExpect(jsonPath("$[0].description").value(TEAM_DESCRIPTION));
    }

    @Test
    void testGetTeamById_Success() throws Exception {
        // Mock repository
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));

        // Perform request
        mockMvc.perform(get("/api/teams/{id}", TEAM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEAM_ID))
                .andExpect(jsonPath("$.name").value(TEAM_NAME))
                .andExpect(jsonPath("$.description").value(TEAM_DESCRIPTION));
    }

    @Test
    void testGetTeamById_NotFound() throws Exception {
        // Mock repository
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Perform request
        mockMvc.perform(get("/api/teams/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTeam_Success() throws Exception {
        // Create team request
        TeamRequest teamRequest = new TeamRequest();
        teamRequest.setName(TEAM_NAME);
        teamRequest.setDescription(TEAM_DESCRIPTION);
        Set<Long> memberIds = new HashSet<>();
        memberIds.add(USER_ID);
        teamRequest.setMemberIds(memberIds);

        // Mock repository methods
        when(teamRepository.existsByName(TEAM_NAME)).thenReturn(false);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        // Perform request
        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Team created successfully!"));

        // Verify that the team was saved
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void testCreateTeam_NameAlreadyTaken() throws Exception {
        // Create team request
        TeamRequest teamRequest = new TeamRequest();
        teamRequest.setName(TEAM_NAME);
        teamRequest.setDescription(TEAM_DESCRIPTION);

        // Mock repository methods
        when(teamRepository.existsByName(TEAM_NAME)).thenReturn(true);

        // Perform request
        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Team name is already taken!"));

        // Verify that no team was saved
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testUpdateTeam_Success() throws Exception {
        // Create team request
        TeamRequest teamRequest = new TeamRequest();
        teamRequest.setName("Updated Team Name");
        teamRequest.setDescription("Updated Team Description");
        Set<Long> memberIds = new HashSet<>();
        memberIds.add(USER_ID);
        teamRequest.setMemberIds(memberIds);

        // Mock repository methods
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(teamRepository.existsByName("Updated Team Name")).thenReturn(false);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        // Perform request
        mockMvc.perform(put("/api/teams/{id}", TEAM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Team updated successfully!"));

        // Verify that the team was saved
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void testUpdateTeam_NameAlreadyTaken() throws Exception {
        // Create team request with a name that's already taken
        TeamRequest teamRequest = new TeamRequest();
        teamRequest.setName("Existing Team Name");
        teamRequest.setDescription(TEAM_DESCRIPTION);

        // Mock repository methods
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(teamRepository.existsByName("Existing Team Name")).thenReturn(true);

        // Perform request
        mockMvc.perform(put("/api/teams/{id}", TEAM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Team name is already taken!"));

        // Verify that no team was saved
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testUpdateTeam_NotFound() throws Exception {
        // Create team request
        TeamRequest teamRequest = new TeamRequest();
        teamRequest.setName(TEAM_NAME);
        teamRequest.setDescription(TEAM_DESCRIPTION);

        // Mock repository methods
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Perform request
        mockMvc.perform(put("/api/teams/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamRequest)))
                .andExpect(status().isNotFound());

        // Verify that no team was saved
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testDeleteTeam_Success() throws Exception {
        // Mock repository methods
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));

        // Perform request
        mockMvc.perform(delete("/api/teams/{id}", TEAM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Team deleted successfully!"));

        // Verify that the team was deleted
        verify(teamRepository).delete(team);
    }

    @Test
    void testDeleteTeam_NotFound() throws Exception {
        // Mock repository methods
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Perform request
        mockMvc.perform(delete("/api/teams/{id}", 999L))
                .andExpect(status().isNotFound());

        // Verify that no team was deleted
        verify(teamRepository, never()).delete(any(Team.class));
    }

    @Test
    void testAddMemberToTeam_Success() throws Exception {
        // Mock repository methods
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Perform request
        mockMvc.perform(put("/api/teams/{teamId}/members/{userId}", TEAM_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User added to team successfully!"));

        // Verify that the user was saved
        verify(userRepository).save(user);
    }

    @Test
    void testAddMemberToTeam_TeamNotFound() throws Exception {
        // Mock repository methods
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Perform request
        mockMvc.perform(put("/api/teams/{teamId}/members/{userId}", 999L, USER_ID))
                .andExpect(status().isNotFound());

        // Verify that no user was saved
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddMemberToTeam_UserNotFound() throws Exception {
        // Mock repository methods
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Perform request
        mockMvc.perform(put("/api/teams/{teamId}/members/{userId}", TEAM_ID, 999L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: User not found!"));

        // Verify that no user was saved
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRemoveMemberFromTeam_Success() throws Exception {
        // Set up user with team
        user.setTeam(team);

        // Mock repository methods
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Perform request
        mockMvc.perform(delete("/api/teams/{teamId}/members/{userId}", TEAM_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User removed from team successfully!"));

        // Verify that the user was saved
        verify(userRepository).save(user);
    }

    @Test
    void testRemoveMemberFromTeam_TeamNotFound() throws Exception {
        // Mock repository methods
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Perform request
        mockMvc.perform(delete("/api/teams/{teamId}/members/{userId}", 999L, USER_ID))
                .andExpect(status().isNotFound());

        // Verify that no user was saved
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRemoveMemberFromTeam_UserNotInTeam() throws Exception {
        // User is not in the team
        user.setTeam(null);

        // Mock repository methods
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Perform request
        mockMvc.perform(delete("/api/teams/{teamId}/members/{userId}", TEAM_ID, USER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: User is not a member of this team!"));

        // Verify that no user was saved
        verify(userRepository, never()).save(any(User.class));
    }
}