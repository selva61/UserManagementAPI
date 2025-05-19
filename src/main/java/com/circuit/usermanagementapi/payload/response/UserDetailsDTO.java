package com.circuit.usermanagementapi.payload.response;

import com.circuit.usermanagementapi.model.User;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Detailed Data Transfer Object for User entity
 * Contains more user information but still excludes sensitive data
 */
public class UserDetailsDTO extends UserDTO {
    private String email;
    private Map<String, String> preferences;

    public UserDetailsDTO() {
        super();
    }

    /**
     * Constructs a UserDetailsDTO from a User entity
     * @param user The User entity
     */
    public UserDetailsDTO(User user) {
        super(user);
        this.email = user.getEmail();
        
        // Copy preferences if available
        if (user.getPreferences() != null) {
            this.preferences = Map.copyOf(user.getPreferences());
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }
}