package com.circuit.usermanagementapi.payload.response;

import com.circuit.usermanagementapi.model.Team;
import com.circuit.usermanagementapi.model.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for Team entity
 * Contains team information and a list of UserDTO objects for members
 * to avoid exposing sensitive user data
 */
public class TeamDTO {
    private Long id;
    private String name;
    private String description;
    private Set<UserDTO> members = new HashSet<>();

    public TeamDTO() {
    }

    /**
     * Constructs a TeamDTO from a Team entity
     * @param team The Team entity
     */
    public TeamDTO(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.description = team.getDescription();
        
        // Convert User entities to UserDTOs to avoid exposing sensitive data
        if (team.getMembers() != null) {
            this.members = team.getMembers().stream()
                .map(UserDTO::new)
                .collect(Collectors.toSet());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<UserDTO> getMembers() {
        return members;
    }

    public void setMembers(Set<UserDTO> members) {
        this.members = members;
    }
}