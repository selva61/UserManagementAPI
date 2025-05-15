package com.circuit.usermanagementapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TestController testController;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
    }

    @Test
    void testAllAccess() throws Exception {
        // Perform request
        mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isOk())
                .andExpect(content().string("Public Content."));
    }

    @Test
    void testUserAccess() throws Exception {
        // Perform request
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Content."));
    }

    @Test
    void testProductOwnerAccess() throws Exception {
        // Perform request
        mockMvc.perform(get("/api/test/po"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product Owner Board."));
    }

    @Test
    void testScrumMasterAccess() throws Exception {
        // Perform request
        mockMvc.perform(get("/api/test/sm"))
                .andExpect(status().isOk())
                .andExpect(content().string("Scrum Master Board."));
    }
}