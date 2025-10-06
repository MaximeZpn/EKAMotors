package com.gp9.carte_MS.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.junit.jupiter.api.Assertions.*;

class DiagnosticControllerTest {
    
    private DiagnosticController controller = new DiagnosticController();
    
    @Test
    void getInfo_ShouldReturnSystemInfo() {
        ResponseEntity<?> response = controller.getInfo();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
    }
    
    @Test
    void echo_ShouldReturnRequestInfo() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("localhost");
        request.setServerPort(8080);
        
        ResponseEntity<?> response = controller.echo(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
}
