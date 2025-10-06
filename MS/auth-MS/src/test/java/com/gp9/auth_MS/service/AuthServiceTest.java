package com.gp9.auth_MS.service;

import com.gp9.auth_MS.model.AuthRequest;
import com.gp9.auth_MS.model.AuthResponse;
import com.gp9.auth_MS.model.UserCredential;
import com.gp9.auth_MS.repository.UserCredentialRepository;
import com.gp9.auth_MS.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserCredentialRepository userCredentialRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private UserCredential testUser;
    private AuthRequest testAuthRequest;
    private static final String TEST_TOKEN = "test.jwt.token";

    @BeforeEach
    void setUp() {
        testUser = new UserCredential();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("hashedPassword");
        testUser.setRole("USER");

        testAuthRequest = new AuthRequest();
        testAuthRequest.setUsername("testuser");
        testAuthRequest.setPassword("password123");
    }

    @Nested
    class AuthenticationTests {
        
        @Test
        void authenticate_ShouldSucceed_WithValidCredentials() {
            // Given
            when(userCredentialRepository.findByUsername(testAuthRequest.getUsername()))
                .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(testAuthRequest.getPassword(), testUser.getPassword()))
                .thenReturn(true);
            when(jwtUtil.generateToken(anyString(), any(), anyString()))
                .thenReturn(TEST_TOKEN);

            // When
            AuthResponse response = authService.authenticate(testAuthRequest);

            // Then
            assertNotNull(response);
            assertEquals(TEST_TOKEN, response.getToken());
            assertEquals(testUser.getUsername(), response.getUsername());
        }

        @Test
        void authenticate_ShouldFail_WithInvalidCredentials() {
            // Given
            when(userCredentialRepository.findByUsername(testAuthRequest.getUsername()))
                .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

            // When/Then
            assertThrows(RuntimeException.class, () -> 
                authService.authenticate(testAuthRequest)
            );
        }

        @Test
        void authenticate_ShouldFail_WhenUserNotFound() {
            // Given
            when(userCredentialRepository.findByUsername(testAuthRequest.getUsername()))
                .thenReturn(Optional.empty());

            // When/Then
            assertThrows(RuntimeException.class, () -> 
                authService.authenticate(testAuthRequest)
            );
        }

        @Test
        void authenticate_ShouldFail_WhenPasswordIsNull() {
            // Given
            testAuthRequest.setPassword(null);

            // When/Then
            assertThrows(RuntimeException.class, () -> 
                authService.authenticate(testAuthRequest)
            );
        }
    }

    @Nested
    class RegistrationTests {

        @Test
        void register_ShouldSucceed_WithValidData() {
            // Given
            when(userCredentialRepository.existsByUsername(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
            when(userCredentialRepository.save(any(UserCredential.class))).thenReturn(testUser);

            // When
            UserCredential result = authService.register(testAuthRequest);

            // Then
            assertNotNull(result);
            assertEquals(testUser.getUsername(), result.getUsername());
        }

        @Test
        void register_ShouldFail_WithExistingUsername() {
            // Given
            when(userCredentialRepository.existsByUsername(anyString())).thenReturn(true);

            // Then
            assertThrows(RuntimeException.class, () ->
                authService.register(testAuthRequest)
            );
        }

        @Test
        void register_ShouldSetDefaultRole() {
            // Given
            String username = "newuser";
            String password = "password123";
            when(userCredentialRepository.existsByUsername(username)).thenReturn(false);
            when(passwordEncoder.encode(password)).thenReturn("hashedPassword");
            when(userCredentialRepository.save(any())).thenReturn(testUser);

            // When
            UserCredential result = authService.register(username, password, "USER");

            // Then
            assertEquals("USER", result.getRole());
            verify(userCredentialRepository).save(any());
        }

        @Test
        void register_ShouldFail_WithNullUsername() {
            // Given
            testAuthRequest.setUsername(null);

            // When/Then
            assertThrows(RuntimeException.class, () ->
                authService.register(testAuthRequest)
            );
        }

        @Test
        void register_ShouldFail_WithEmptyPassword() {
            // Given
            testAuthRequest.setPassword("");

            // When/Then
            assertThrows(RuntimeException.class, () ->
                authService.register(testAuthRequest)
            );
        }
    }

    @Nested
    class TokenValidationTests {
        
        @Test
        void validateToken_ShouldReturnFalse_WhenTokenExpired() {
            // Given
            String username = "testuser";
            UserCredential user = new UserCredential();
            user.setUsername(username);
            
            when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(username);
            when(userCredentialRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
            when(jwtUtil.validateToken(TEST_TOKEN, username)).thenReturn(false);

            // When
            boolean result = authService.validateToken(TEST_TOKEN);

            // Then
            assertFalse(result);
            verify(jwtUtil).validateToken(TEST_TOKEN, username);
        }

        @Test
        void validateToken_ShouldReturnFalse_WhenUserNotFound() {
            // Given
            when(jwtUtil.extractUsername(anyString())).thenReturn("nonexistent");
            when(userCredentialRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

            // When
            boolean result = authService.validateToken("token");

            // Then
            assertFalse(result);
        }

        @Test
        void validateToken_ShouldReturnFalse_WhenTokenIsNull() {
            // When
            boolean result = authService.validateToken(null);

            // Then
            assertFalse(result);
        }

        @Test
        void validateToken_ShouldReturnTrue_WhenValid() {
            // Given
            String username = "validuser";
            UserCredential user = new UserCredential();
            user.setUsername(username);
            
            when(jwtUtil.extractUsername("valid-token")).thenReturn(username);
            when(userCredentialRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
            when(jwtUtil.validateToken("valid-token", username)).thenReturn(true);

            // When
            boolean result = authService.validateToken("valid-token");

            // Then
            assertTrue(result);
        }
    }
}
