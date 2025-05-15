package com.circuit.usermanagementapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.SpringApplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class UserManagementApiApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testMainMethod() {
        try (var mocked = mockStatic(SpringApplication.class)) {
            // Call the main method
            UserManagementApiApplication.main(new String[]{});

            // Verify that SpringApplication.run was called with the correct arguments
            mocked.verify(() -> 
                SpringApplication.run(eq(UserManagementApiApplication.class), any(String[].class))
            );
        }
    }
}
