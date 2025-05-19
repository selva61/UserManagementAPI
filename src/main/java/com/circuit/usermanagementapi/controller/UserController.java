package com.circuit.usermanagementapi.controller;

import com.circuit.usermanagementapi.model.Team;
import com.circuit.usermanagementapi.model.User;
import com.circuit.usermanagementapi.payload.response.MessageResponse;
import com.circuit.usermanagementapi.payload.response.UserDTO;
import com.circuit.usermanagementapi.payload.response.UserDetailsDTO;
import com.circuit.usermanagementapi.repository.TeamRepository;
import com.circuit.usermanagementapi.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API endpoints for managing users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    TeamRepository teamRepository;

    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user list",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class)))
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SCRUM_MASTER') or hasAuthority('ROLE_PRODUCT_OWNER')")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailsDTO.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SCRUM_MASTER') or hasAuthority('ROLE_PRODUCT_OWNER') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<?> getUserById(@Parameter(description = "ID of the user to retrieve") @PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok().body(new UserDetailsDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get current user", description = "Retrieves the currently authenticated user's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved current user",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailsDTO.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SCRUM_MASTER') or hasAuthority('ROLE_PRODUCT_OWNER') or hasAuthority('ROLE_TEAM_MEMBER')")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .map(user -> ResponseEntity.ok().body(new UserDetailsDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update user preferences", description = "Updates the preferences for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User preferences updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PutMapping("/{id}/preferences")
    @PreAuthorize("@userSecurity.isCurrentUser(#id)")
    public ResponseEntity<?> updateUserPreferences(
            @Parameter(description = "ID of the user to update") @PathVariable Long id, 
            @Parameter(description = "User preferences to update") @RequestBody Map<String, String> preferences) {
        return userRepository.findById(id)
                .map(user -> {
                    Map<String, String> userPrefs = new HashMap<>(user.getPreferences());
                    userPrefs.putAll(preferences);
                    user.setPreferences(userPrefs);
                    userRepository.save(user);
                    return ResponseEntity.ok().body(new MessageResponse("User preferences updated successfully!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete user", description = "Deletes a specific user from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SCRUM_MASTER')")
    public ResponseEntity<?> deleteUser(@Parameter(description = "ID of the user to delete") @PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().body(new MessageResponse("User deleted successfully!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update user team", description = "Updates the team assignment for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User team updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Team not found", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PutMapping("/{id}/team")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SCRUM_MASTER') or hasAuthority('ROLE_PRODUCT_OWNER') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<?> updateUserTeam(
            @Parameter(description = "ID of the user to update") @PathVariable Long id, 
            @Parameter(description = "Team update request") @RequestBody UpdateTeamRequest updateTeamRequest) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (updateTeamRequest.getTeamId() == null) {
            // Remove user from current team
            user.setTeam(null);
            userRepository.save(user);
            return ResponseEntity.ok().body(new MessageResponse("User removed from team successfully!"));
        } else {
            // Add user to new team
            Team team = teamRepository.findById(updateTeamRequest.getTeamId()).orElse(null);
            if (team == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Team not found!"));
            }

            user.setTeam(team);
            userRepository.save(user);
            return ResponseEntity.ok().body(new MessageResponse("User team updated successfully!"));
        }
    }

    // Request class for updating a user's team
    @Schema(description = "Request object for updating a user's team assignment")
    public static class UpdateTeamRequest {
        @Schema(description = "ID of the team to assign to the user. If null, removes user from current team")
        private Long teamId;

        public Long getTeamId() {
            return teamId;
        }

        public void setTeamId(Long teamId) {
            this.teamId = teamId;
        }
    }
}
