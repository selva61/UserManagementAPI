package com.circuit.usermanagementapi.payload.response;

import com.circuit.usermanagementapi.model.Role;
import com.circuit.usermanagementapi.model.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for User entity
 * Contains only non-sensitive user information
 */
public class UserDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Set<String> roles;
    private Long teamId;
    private String teamName;

    public UserDTO() {
    }

    /**
     * Constructs a UserDTO from a User entity
     * @param user The User entity
     */
    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();

        // Extract role names
        if (user.getRoles() != null) {
            this.roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        }

        // Extract team information if available
        if (user.getTeam() != null) {
            this.teamId = user.getTeam().getId();
            this.teamName = user.getTeam().getName();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
