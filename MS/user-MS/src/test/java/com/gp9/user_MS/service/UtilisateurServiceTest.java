package com.gp9.user_MS.service;

import com.gp9.user_MS.dto.CreateUtilisateurDTO;
import com.gp9.user_MS.dto.UtilisateurDTO;
import com.gp9.user_MS.model.Utilisateur;
import com.gp9.user_MS.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Utilisateur Service Tests")
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private UtilisateurService utilisateurService;

    private Utilisateur testUtilisateur;
    private CreateUtilisateurDTO createDTO;

    @BeforeEach
    void setUp() {
        testUtilisateur = new Utilisateur();
        testUtilisateur.setId(1L);
        testUtilisateur.setUsername("testuser");
        testUtilisateur.setSolde(100.0);

        createDTO = new CreateUtilisateurDTO();
        createDTO.setUsername("newuser");
        createDTO.setEmail("newuser@test.com");
    }

    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {
        @Test
        void creerUtilisateur_ShouldCreateSuccessfully() {
            when(utilisateurRepository.existsByUsername(anyString())).thenReturn(false);
            when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(testUtilisateur);

            Utilisateur result = utilisateurService.creerUtilisateur(createDTO);

            assertNotNull(result);
            assertEquals(testUtilisateur.getId(), result.getId());
            verify(utilisateurRepository).save(any(Utilisateur.class));
        }

        @Test
        void creerUtilisateur_ShouldFailWithDuplicateUsername() {
            when(utilisateurRepository.existsByUsername(anyString())).thenReturn(true);

            assertThrows(RuntimeException.class, () -> 
                utilisateurService.creerUtilisateur(createDTO)
            );
        }
    }

    @Nested
    @DisplayName("Balance Management Tests")
    class BalanceTests {
        @Test
        void modifierSolde_ShouldUpdateBalance() {
            when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(testUtilisateur));
            when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(testUtilisateur);

            double amount = 50.0;
            boolean result = utilisateurService.modifierSolde(1L, amount);

            assertTrue(result);
            assertEquals(150.0, testUtilisateur.getSolde());
        }

        @Test
        void modifierSolde_ShouldFailForInvalidUser() {
            when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());

            boolean result = utilisateurService.modifierSolde(1L, 50.0);

            assertFalse(result);
        }
    }

    @Test
    void getUtilisateurById_ShouldReturnDTO() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(testUtilisateur));

        Optional<UtilisateurDTO> result = utilisateurService.getUtilisateurById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUtilisateur.getId(), result.get().getId());
        assertEquals(testUtilisateur.getUsername(), result.get().getUsername());
    }

    @Test
    void getAllUtilisateurs_ShouldReturnList() {
        when(utilisateurRepository.findAll()).thenReturn(Arrays.asList(testUtilisateur));

        List<UtilisateurDTO> result = utilisateurService.getAllUtilisateurs();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testUtilisateur.getId(), result.get(0).getId());
    }
}
