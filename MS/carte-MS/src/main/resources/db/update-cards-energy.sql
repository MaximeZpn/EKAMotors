-- Update script to add energy to existing cards
ALTER TABLE cartes ADD COLUMN IF NOT EXISTS energy INT DEFAULT 100;
ALTER TABLE cartes ADD COLUMN IF NOT EXISTS last_energy_regen TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE cartes ADD COLUMN IF NOT EXISTS type VARCHAR(20) DEFAULT 'NORMAL';

-- Update existing cards to have full energy
UPDATE cartes SET energy = 100 WHERE energy IS NULL;
UPDATE cartes SET last_energy_regen = CURRENT_TIMESTAMP WHERE last_energy_regen IS NULL;

-- Randomly assign card types to existing cards
UPDATE cartes SET type = ELT(FLOOR(RAND() * 5) + 1, 'FIRE', 'WATER', 'EARTH', 'AIR', 'NORMAL') WHERE type IS NULL OR type = '';
