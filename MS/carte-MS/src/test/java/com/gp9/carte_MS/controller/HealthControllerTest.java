package com.gp9.carte_MS.controller;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class HealthControllerTest {
    private HealthController healthController = new HealthController();

    @Test
    void healthCheck_ShouldReturnStatus() {
        Map<String, Object> result = healthController.healthCheck();
        assertNotNull(result);
        assertEquals("UP", result.get("status"));
    }

    @Test
    void debug_ShouldReturnDebugInfo() {
        Map<String, Object> result = healthController.debug();
        assertNotNull(result);
        assertTrue(result.containsKey("processors"));
        assertTrue(result.containsKey("freeMemory"));
        assertTrue(result.containsKey("maxMemory"));
        assertTrue(result.containsKey("totalMemory"));
    }
}
