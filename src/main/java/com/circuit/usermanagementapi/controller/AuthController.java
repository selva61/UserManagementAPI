package com.circuit.usermanagementapi.controller;

import com.circuit.usermanagementapi.model.ERole;
import com.circuit.usermanagementapi.model.Role;
import com.circuit.usermanagementapi.model.User;
import com.circuit.usermanagementapi.payload.request.LoginRequest;
import com.circuit.usermanagementapi.payload.request.SignupRequest;
import com.circuit.usermanagementapi.payload.response.JwtResponse;
import com.circuit.usermanagementapi.payload.response.MessageResponse;
import com.circuit.usermanagementapi.repository.RoleRepository;
import com.circuit.usermanagementapi.repository.TeamRepository;
import com.circuit.usermanagementapi.repository.UserRepository;
import com.circuit.usermanagementapi.security.jwt.JwtUtils;
import com.circuit.usermanagementapi.security.services.TokenBlacklistService;
import com.circuit.usermanagementapi.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    TokenBlacklistService tokenBlacklistService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                                                 userDetails.getId(),
                                                 userDetails.getUsername(),
                                                 userDetails.getEmail(),
                                                 roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                             signUpRequest.getEmail(),
                             encoder.encode(signUpRequest.getPassword()));

        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());

        // Check if team exists and set it
        if (signUpRequest.getTeamId() != null) {
            teamRepository.findById(signUpRequest.getTeamId()).ifPresent(team -> {
                user.setTeam(team);
            });
        }

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_TEAM_MEMBER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "scrum_master":
                        Role scrumMasterRole = roleRepository.findByName(ERole.ROLE_SCRUM_MASTER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(scrumMasterRole);
                        break;
                    case "product_owner":
                        Role productOwnerRole = roleRepository.findByName(ERole.ROLE_PRODUCT_OWNER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(productOwnerRole);
                        break;
                    default:
                        Role teamMemberRole = roleRepository.findByName(ERole.ROLE_TEAM_MEMBER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(teamMemberRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    /**
     * Logs out a user by invalidating their JWT token
     * 
     * @param request The HTTP request containing the JWT token
     * @return ResponseEntity with a success message
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        String jwt = parseJwt(request);

        if (jwt != null && StringUtils.hasText(jwt)) {
            try {
                // Get token expiration time
                long expirationTime = jwtUtils.getExpirationFromJwtToken(jwt);

                // Add token to blacklist
                tokenBlacklistService.blacklistToken(jwt, expirationTime);

                // Clear security context
                SecurityContextHolder.clearContext();

                return ResponseEntity.ok(new MessageResponse("Logout successful!"));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
            }
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Error: No token provided"));
    }

    /**
     * Extracts the JWT token from the request
     * 
     * @param request The HTTP request
     * @return The JWT token or null if not found
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
