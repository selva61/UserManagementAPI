package com.circuit.usermanagementapi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('TEAM_MEMBER') or hasRole('PRODUCT_OWNER') or hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/po")
    @PreAuthorize("hasRole('PRODUCT_OWNER') or hasRole('ADMIN')")
    public String productOwnerAccess() {
        return "Product Owner Board.";
    }

    @GetMapping("/sm")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public String scrumMasterAccess() {
        return "Scrum Master Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board. Full access to all features.";
    }
}
