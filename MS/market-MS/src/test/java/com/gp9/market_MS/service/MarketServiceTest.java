package com.gp9.market_MS.service;

import com.gp9.market_MS.client.CarteClient;
import com.gp9.market_MS.client.UserClient;
import com.gp9.market_MS.dto.CarteDTO;
import com.gp9.market_MS.dto.OffreDTO;
import com.gp9.market_MS.model.Offre;
import com.gp9.market_MS.model.Transaction;
import com.gp9.market_MS.repository.OffreRepository;
import com.gp9.market_MS.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Market Service Tests")
class MarketServiceTest {

    @Mock
    private OffreRepository offreRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CarteClient carteClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private MarketService marketService;

    private Offre testOffre;
    private CarteDTO testCarteDTO;
    private OffreDTO testOffreDTO;
    private static final Long SELLER_ID = 1L;
    private static final Long BUYER_ID = 2L;

    @BeforeEach
    void setUp() {
        testOffre = new Offre();
        testOffre.setId(1L);
        testOffre.setCarteId(1L);
        testOffre.setVendeurId(SELLER_ID);
        testOffre.setPrix(100.0);
        testOffre.setActive(true);

        testCarteDTO = new CarteDTO();
        testCarteDTO.setId(1L);
        testCarteDTO.setProprietaireId(SELLER_ID);
    }

    @Nested
    @DisplayName("Offer Management Tests")
    class OfferTests {
        @Test
        void acheterCarte_ShouldCompleteTransaction() {
            // Given
            when(offreRepository.findById(1L)).thenReturn(Optional.of(testOffre));
            when(carteClient.getCarteById(1L)).thenReturn(testCarteDTO);
            when(userClient.modifierSolde(anyLong(), anyDouble())).thenReturn(true);
            when(carteClient.updateCarteOwnership(anyLong(), anyLong())).thenReturn(true);
            // Mock la sauvegarde de la transaction
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
                Transaction t = invocation.getArgument(0);
                t.setId(1L); // Simuler l'attribution d'un ID
                return t;
            });

            // When/Then
            assertDoesNotThrow(() -> marketService.acheterCarte(1L, BUYER_ID));

            // Verify
            verify(transactionRepository).save(any(Transaction.class));
            verify(offreRepository).save(any(Offre.class));
        }
    }
}
