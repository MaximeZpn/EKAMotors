    package com.gp9.atelier2.service;

    import com.gp9.atelier2.dto.CarteDTO;
    import com.gp9.atelier2.model.Carte;
    import com.gp9.atelier2.repository.CarteRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    public class CarteService {

        @Autowired
        private CarteRepository carteRepository;

        public Optional<CarteDTO> getCarteById(Long id) {
            return carteRepository.findById(id).map(CarteDTO::new);
        }

        public List<CarteDTO> getCartesByUtilisateur(Long utilisateurId) {
            List<Carte> cartes = carteRepository.findByProprietaireId(utilisateurId);
            return cartes.stream().map(CarteDTO::new).collect(Collectors.toList());
        }

    }
