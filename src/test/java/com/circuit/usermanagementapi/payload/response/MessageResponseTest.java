package com.circuit.usermanagementapi.payload.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageResponseTest {

    @Test
    void testConstructorAndGetter() {
        String message = "Test message";
        MessageResponse response = new MessageResponse(message);
        
        assertEquals(message, response.getMessage());
    }

    @Test
    void testSetter() {
        String initialMessage = "Initial message";
        String newMessage = "New message";
        
        MessageResponse response = new MessageResponse(initialMessage);
        assertEquals(initialMessage, response.getMessage());
        
        response.setMessage(newMessage);
        assertEquals(newMessage, response.getMessage());
    }
}