-- Rebranding des cartes en véhicules réels pour EKA Motors
-- Cette migration renomme les enregistrements existants avec des modèles, descriptions et images automobiles.

-- Met à jour tous les enregistrements non encore rebrandés (nom ne commençant pas par 'EKA ')
UPDATE cartes
SET 
  nom = CONCAT('EKA Nova ', id),
  description = 'Véhicule EKA Motors, fiable et prêt à rouler. Contrôle OK.',
  imageUrl = 'https://images.pexels.com/photos/210019/pexels-photo-210019.jpeg',
  rarete = COALESCE(rarete, 'COMMUN'),
  type = COALESCE(type, 'NORMAL'),
  energy = CASE WHEN energy IS NULL OR energy <= 0 THEN 100 ELSE energy END
WHERE nom NOT LIKE 'EKA %';

-- Optionnel: applique une image différente par motif d'id pour varier visuellement
UPDATE cartes SET imageUrl = 'https://images.pexels.com/photos/799443/pexels-photo-799443.jpeg' WHERE MOD(id, 3) = 0;
UPDATE cartes SET imageUrl = 'https://images.pexels.com/photos/358070/pexels-photo-358070.jpeg' WHERE MOD(id, 5) = 0;
UPDATE cartes SET imageUrl = 'https://images.pexels.com/photos/97075/pexels-photo-97075.jpeg' WHERE MOD(id, 7) = 0;



