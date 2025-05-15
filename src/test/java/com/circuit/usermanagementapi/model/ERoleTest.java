package com.circuit.usermanagementapi.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ERoleTest {

    @Test
    void testERoleValues() {
        // Test that the enum has exactly 4 values
        assertEquals(4, ERole.values().length);

        // Test that the enum contains the expected values
        assertNotNull(ERole.valueOf("ROLE_ADMIN"));
        assertNotNull(ERole.valueOf("ROLE_SCRUM_MASTER"));
        assertNotNull(ERole.valueOf("ROLE_PRODUCT_OWNER"));
        assertNotNull(ERole.valueOf("ROLE_TEAM_MEMBER"));
    }

    @Test
    void testERoleOrdinals() {
        // Test the ordinal values of the enum constants
        assertEquals(0, ERole.ROLE_ADMIN.ordinal());
        assertEquals(1, ERole.ROLE_SCRUM_MASTER.ordinal());
        assertEquals(2, ERole.ROLE_PRODUCT_OWNER.ordinal());
        assertEquals(3, ERole.ROLE_TEAM_MEMBER.ordinal());
    }
}
