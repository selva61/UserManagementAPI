package com.circuit.usermanagementapi.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class TeamRequest {
    
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;
    
    @Size(max = 200)
    private String description;
    
    private Set<Long> memberIds;
    
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
    
    public Set<Long> getMemberIds() {
        return memberIds;
    }
    
    public void setMemberIds(Set<Long> memberIds) {
        this.memberIds = memberIds;
    }
}