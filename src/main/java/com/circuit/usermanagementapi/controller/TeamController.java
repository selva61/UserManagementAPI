package com.circuit.usermanagementapi.controller;

import com.circuit.usermanagementapi.model.Team;
import com.circuit.usermanagementapi.model.User;
import com.circuit.usermanagementapi.payload.request.TeamRequest;
import com.circuit.usermanagementapi.payload.response.MessageResponse;
import com.circuit.usermanagementapi.payload.response.TeamDTO;
import com.circuit.usermanagementapi.repository.TeamRepository;
import com.circuit.usermanagementapi.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teams")
public class TeamController {
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SCRUM_MASTER') or hasAuthority('ROLE_PRODUCT_OWNER') or hasAuthority('ROLE_TEAM_MEMBER')")
    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(TeamDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SCRUM_MASTER') or hasAuthority('ROLE_PRODUCT_OWNER') or hasAuthority('ROLE_TEAM_MEMBER')")
    public ResponseEntity<?> getTeamById(@PathVariable Long id) {
        return teamRepository.findById(id)
                .map(team -> ResponseEntity.ok().body(new TeamDTO(team)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SCRUM_MASTER') or hasAuthority('ROLE_PRODUCT_OWNER')")
    public ResponseEntity<?> createTeam(@Valid @RequestBody TeamRequest teamRequest) {
        if (teamRepository.existsByName(teamRequest.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Team name is already taken!"));
        }

        Team team = new Team(teamRequest.getName(), teamRequest.getDescription());

        if (teamRequest.getMemberIds() != null && !teamRequest.getMemberIds().isEmpty()) {
            Set<User> members = new HashSet<>();
            teamRequest.getMemberIds().forEach(userId -> {
                userRepository.findById(userId).ifPresent(user -> {
                    user.setTeam(team);
                    members.add(user);
                });
            });
            team.setMembers(members);
        }

        teamRepository.save(team);

        return ResponseEntity.ok(new MessageResponse("Team created successfully!"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SCRUM_MASTER') or hasAuthority('ROLE_PRODUCT_OWNER')")
    public ResponseEntity<?> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequest teamRequest) {
        return teamRepository.findById(id)
                .map(team -> {
                    // Check if the new name is already taken by another team
                    if (!team.getName().equals(teamRequest.getName()) && 
                        teamRepository.existsByName(teamRequest.getName())) {
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("Error: Team name is already taken!"));
                    }

                    team.setName(teamRequest.getName());
                    team.setDescription(teamRequest.getDescription());

                    // Update team members if provided
                    if (teamRequest.getMemberIds() != null) {
                        // Remove current team association for all members
                        team.getMembers().forEach(member -> member.setTeam(null));

                        // Add new members
                        Set<User> members = new HashSet<>();
                        teamRequest.getMemberIds().forEach(userId -> {
                            userRepository.findById(userId).ifPresent(user -> {
                                user.setTeam(team);
                                members.add(user);
                            });
                        });
                        team.setMembers(members);
                    }

                    teamRepository.save(team);
                    return ResponseEntity.ok(new MessageResponse("Team updated successfully!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SCRUM_MASTER')")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        return teamRepository.findById(id)
                .map(team -> {
                    // Remove team association for all members
                    team.getMembers().forEach(member -> member.setTeam(null));

                    teamRepository.delete(team);
                    return ResponseEntity.ok(new MessageResponse("Team deleted successfully!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SCRUM_MASTER') or hasAuthority('ROLE_PRODUCT_OWNER')")
    public ResponseEntity<?> addMemberToTeam(@PathVariable Long teamId, @PathVariable Long userId) {
        Team team = teamRepository.findById(teamId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (team == null) {
            return ResponseEntity.notFound().build();
        }

        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found!"));
        }

        user.setTeam(team);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User added to team successfully!"));
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SCRUM_MASTER') or hasAuthority('ROLE_PRODUCT_OWNER')")
    public ResponseEntity<?> removeMemberFromTeam(@PathVariable Long teamId, @PathVariable Long userId) {
        Team team = teamRepository.findById(teamId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (team == null) {
            return ResponseEntity.notFound().build();
        }

        if (user == null || user.getTeam() == null || !user.getTeam().getId().equals(teamId)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is not a member of this team!"));
        }

        user.setTeam(null);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User removed from team successfully!"));
    }
}
