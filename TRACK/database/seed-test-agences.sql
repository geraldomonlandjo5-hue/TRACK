-- DONNÉES DE TEST UNIQUEMENT — ne jamais exécuter en production.
-- Prérequis : schema.sql, seed-admin.sql puis seed-test-benin.sql.
-- Crée 3 agences, 4 comptes (mot de passe commun : TestTrack2026!) et rattache les colis de test à leur agence.

INSERT INTO agencies (id, code, name, city) VALUES
('00000000-0000-0000-0000-000000000011', 'COT', 'Agence de Cotonou', 'Cotonou'),
('00000000-0000-0000-0000-000000000012', 'PNV', 'Agence de Porto-Novo', 'Porto-Novo'),
('00000000-0000-0000-0000-000000000013', 'PAR', 'Agence de Parakou', 'Parakou')
ON CONFLICT (code) DO NOTHING;

INSERT INTO users (id, agency_id, role_id, full_name, email, password_hash, is_active) VALUES
('00000000-0000-0000-0000-000000000021', '00000000-0000-0000-0000-000000000011', (SELECT id FROM roles WHERE name='AGENT'), 'Agent Cotonou', 'agent.cotonou@track.local', 'pbkdf2_sha256$210000$2ce55a81aaf88a42a6bef2867eeed0e9$cd891d8b850c457532d61a41c6f432d582aa8bc7894f9050f6a687904e4d079a', TRUE),
('00000000-0000-0000-0000-000000000022', '00000000-0000-0000-0000-000000000011', (SELECT id FROM roles WHERE name='RESPONSABLE_AGENCE'), 'Responsable Cotonou', 'responsable.cotonou@track.local', 'pbkdf2_sha256$210000$79a7b68f12a47b268ffa6ca3ed54967c$dcb0460aa2e029ed143928cdc06abf0c370d2c14d7d7fde94cb18655780a4e68', TRUE),
('00000000-0000-0000-0000-000000000023', '00000000-0000-0000-0000-000000000011', (SELECT id FROM roles WHERE name='LIVREUR'), 'Livreur Cotonou', 'livreur.cotonou@track.local', 'pbkdf2_sha256$210000$b6b2af13ac4009c5ebf515171262a3c9$246af49a77afb45b412b33300194da26a9b0a3e3733693e3e7a8c260eb4ad6ba', TRUE),
('00000000-0000-0000-0000-000000000024', '00000000-0000-0000-0000-000000000012', (SELECT id FROM roles WHERE name='AGENT'), 'Agent Porto-Novo', 'agent.portonovo@track.local', 'pbkdf2_sha256$210000$1b879952c8980c7977a98104794620d5$0ab243394d0027a44ffcdce481a8e3a11ef485d39dea5886f827b7ae77dbf0f2', TRUE)
ON CONFLICT (email) DO NOTHING;

UPDATE shipments SET agency_id = '00000000-0000-0000-0000-000000000011'
 WHERE tracking_number LIKE 'TRK-2026-9000%' AND sender_address ILIKE '%Cotonou%';
UPDATE shipments SET agency_id = '00000000-0000-0000-0000-000000000012'
 WHERE tracking_number LIKE 'TRK-2026-9000%' AND sender_address ILIKE '%Porto-Novo%';
UPDATE shipments SET agency_id = '00000000-0000-0000-0000-000000000013'
 WHERE tracking_number LIKE 'TRK-2026-9000%' AND sender_address ILIKE '%Parakou%';
-- Les autres colis de test restent sans agence : visibles uniquement par les administrateurs.
