package com.circuit.usermanagementapi.controller;

import com.circuit.usermanagementapi.model.ERole;
import com.circuit.usermanagementapi.model.Role;
import com.circuit.usermanagementapi.model.Team;
import com.circuit.usermanagementapi.model.User;
import com.circuit.usermanagementapi.payload.request.LoginRequest;
import com.circuit.usermanagementapi.payload.request.SignupRequest;
import com.circuit.usermanagementapi.repository.RoleRepository;
import com.circuit.usermanagementapi.repository.TeamRepository;
import com.circuit.usermanagementapi.repository.UserRepository;
import com.circuit.usermanagementapi.security.jwt.JwtUtils;
import com.circuit.usermanagementapi.security.services.TokenBlacklistService;
import com.circuit.usermanagementapi.security.services.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthController authController;

    private User user;
    private Role role;
    private Team team;
    private final String USERNAME = "testuser";
    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "password123";
    private final String ENCODED_PASSWORD = "encodedPassword123";
    private final Long USER_ID = 1L;
    private final Long TEAM_ID = 1L;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        // Create role
        role = new Role(ERole.ROLE_TEAM_MEMBER);
        role.setId(1L);

        // Create team
        team = new Team("Test Team", "Test Team Description");
        team.setId(TEAM_ID);

        // Create user
        user = new User(USERNAME, EMAIL, ENCODED_PASSWORD);
        user.setId(USER_ID);
        user.setFirstName("Test");
        user.setLastName("User");

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
    }

    @Test
    void testAuthenticateUser_Success() throws Exception {
        // Create login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(USERNAME);
        loginRequest.setPassword(PASSWORD);

        // Mock authentication
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(ERole.ROLE_TEAM_MEMBER.name()));
        UserDetailsImpl userDetails = new UserDetailsImpl(
                USER_ID, USERNAME, EMAIL, ENCODED_PASSWORD, authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("testJwtToken");

        // Perform request
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("testJwtToken"))
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.roles[0]").value(ERole.ROLE_TEAM_MEMBER.name()));
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Create signup request
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(USERNAME);
        signupRequest.setEmail(EMAIL);
        signupRequest.setPassword(PASSWORD);
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        signupRequest.setTeamId(TEAM_ID);
        Set<String> roles = new HashSet<>();
        roles.add("team_member");
        signupRequest.setRoles(roles);

        // Mock repository methods
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(roleRepository.findByName(ERole.ROLE_TEAM_MEMBER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Perform request
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void testRegisterUser_UsernameAlreadyTaken() throws Exception {
        // Create signup request
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(USERNAME);
        signupRequest.setEmail(EMAIL);
        signupRequest.setPassword(PASSWORD);

        // Mock repository methods
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        // Perform request
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }

    @Test
    void testRegisterUser_EmailAlreadyInUse() throws Exception {
        // Create signup request
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(USERNAME);
        signupRequest.setEmail(EMAIL);
        signupRequest.setPassword(PASSWORD);

        // Mock repository methods
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        // Perform request
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));
    }

    @Test
    void testRegisterUser_WithScrumMasterRole() throws Exception {
        // Create signup request
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(USERNAME);
        signupRequest.setEmail(EMAIL);
        signupRequest.setPassword(PASSWORD);
        Set<String> roles = new HashSet<>();
        roles.add("scrum_master");
        signupRequest.setRoles(roles);

        // Create scrum master role
        Role scrumMasterRole = new Role(ERole.ROLE_SCRUM_MASTER);
        scrumMasterRole.setId(2L);

        // Mock repository methods
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findByName(ERole.ROLE_SCRUM_MASTER)).thenReturn(Optional.of(scrumMasterRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Perform request
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void testRegisterUser_WithProductOwnerRole() throws Exception {
        // Create signup request
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(USERNAME);
        signupRequest.setEmail(EMAIL);
        signupRequest.setPassword(PASSWORD);
        Set<String> roles = new HashSet<>();
        roles.add("product_owner");
        signupRequest.setRoles(roles);

        // Create product owner role
        Role productOwnerRole = new Role(ERole.ROLE_PRODUCT_OWNER);
        productOwnerRole.setId(3L);

        // Mock repository methods
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findByName(ERole.ROLE_PRODUCT_OWNER)).thenReturn(Optional.of(productOwnerRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Perform request
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void testRegisterUser_WithDefaultRole() throws Exception {
        // Create signup request
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(USERNAME);
        signupRequest.setEmail(EMAIL);
        signupRequest.setPassword(PASSWORD);
        // No roles specified

        // Mock repository methods
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findByName(ERole.ROLE_TEAM_MEMBER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Perform request
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void testLogoutUser_Success() throws Exception {
        // Test JWT token
        String testToken = "testJwtToken";
        // Future expiration time (current time + 1 hour)
        long expirationTime = System.currentTimeMillis() + 3600000;

        // Mock JwtUtils method
        when(jwtUtils.getExpirationFromJwtToken(anyString())).thenReturn(expirationTime);

        // Mock TokenBlacklistService method
        doNothing().when(tokenBlacklistService).blacklistToken(anyString(), anyLong());

        // Perform request
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful!"));

        // Verify that the token was blacklisted
        verify(tokenBlacklistService).blacklistToken(testToken, expirationTime);
    }

    @Test
    void testLogoutUser_NoToken() throws Exception {
        // Perform request without token
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: No token provided"));
    }
}
