-- TRACK : schéma + données de test (Bénin) — à coller dans pgAdmin / DBeaver / psql (base track_db)
SET client_encoding TO 'UTF8';

CREATE TABLE IF NOT EXISTS shipments (
    id VARCHAR(36) PRIMARY KEY,
    tracking_number VARCHAR(40) NOT NULL UNIQUE,
    sender_name VARCHAR(120) NOT NULL,
    sender_phone VARCHAR(30),
    sender_address VARCHAR(255),
    recipient_name VARCHAR(120) NOT NULL,
    recipient_phone VARCHAR(30),
    recipient_address VARCHAR(255),
    description VARCHAR(500),
    weight NUMERIC(10,2),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_shipments_status ON shipments(status);

CREATE TABLE IF NOT EXISTS tracking_events (
    id VARCHAR(36) PRIMARY KEY,
    shipment_id VARCHAR(36) NOT NULL REFERENCES shipments(id),
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    location VARCHAR(180),
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_tracking_events_shipment ON tracking_events(shipment_id, created_at);

BEGIN;

-- Remplace d'éventuelles anciennes données de test (uniquement les TRK-2026-9000xx)
DELETE FROM tracking_events WHERE shipment_id IN (SELECT id FROM shipments WHERE tracking_number LIKE 'TRK-2026-9000%');
DELETE FROM shipments WHERE tracking_number LIKE 'TRK-2026-9000%';

INSERT INTO shipments (id, tracking_number, sender_name, sender_phone, sender_address, recipient_name, recipient_phone, recipient_address, description, weight, status, created_at, updated_at) VALUES
('00000000-0000-4000-8000-000000000001', 'TRK-2026-900001', 'Boutique Wax & Co', '+229 01 97 00 11 01', 'Marché Dantokpa, Cotonou', 'Adjoa Houngbo', '+229 01 96 00 22 01', 'Quartier Zongo, Parakou', 'Pagnes wax (6 pièces)', 3.2, 'CANCELLED', now() - interval '288 hours', now() - interval '236 hours'),
('00000000-0000-4000-8000-000000000002', 'TRK-2026-900002', 'Librairie Le Savoir', '+229 01 95 00 11 02', 'Quartier Ouando, Porto-Novo', 'Codjo Mensah', '+229 01 94 00 22 02', 'Quartier Gah, Natitingou', 'Carton de 15 manuels scolaires', 11.5, 'RETURNED', now() - interval '240 hours', now() - interval '120 hours'),
('00000000-0000-4000-8000-000000000003', 'TRK-2026-900003', 'Ets Tchibozo & Fils', '+229 01 97 00 11 03', 'Quartier Akpakpa, Cotonou', 'Rachidatou Sanni', '+229 01 91 00 22 03', 'Arrondissement Banikanni, Parakou', 'Pièces détachées pour moto', 7.8, 'DELIVERY_FAILED', now() - interval '200 hours', now() - interval '150 hours'),
('00000000-0000-4000-8000-000000000004', 'TRK-2026-900004', 'Atelier Karité Naturel', '+229 01 96 00 11 04', 'Quartier Albarika, Parakou', 'Fatouma Alassane', '+229 01 97 00 22 04', 'Quartier Cadjehoun, Cotonou', 'Carton de produits cosmétiques au karité', 5.4, 'DELIVERED', now() - interval '150 hours', now() - interval '104 hours'),
('00000000-0000-4000-8000-000000000005', 'TRK-2026-900005', 'Mode Fidjrossè', '+229 01 95 00 11 05', 'Quartier Fidjrossè, Cotonou', 'Mariam Bio Sika', '+229 01 94 00 22 05', 'Quartier Bakpérou, Djougou', 'Boubous et tenues de cérémonie', 2.1, 'OUT_FOR_DELIVERY', now() - interval '110 hours', now() - interval '70 hours'),
('00000000-0000-4000-8000-000000000006', 'TRK-2026-900006', 'TechBénin Services', '+229 01 91 00 11 06', 'Quartier Haie Vive, Cotonou', 'Wenceslas Agbodjan', '+229 01 96 00 22 06', 'Quartier Ganhi, Abomey-Calavi', 'Smartphone et accessoires', 0.7, 'READY_FOR_PICKUP', now() - interval '80 hours', now() - interval '54 hours'),
('00000000-0000-4000-8000-000000000007', 'TRK-2026-900007', 'Pharmacie de la Paix', '+229 01 97 00 11 07', 'Quartier Gbégamey, Cotonou', 'Zouberath Akpovi', '+229 01 95 00 22 07', 'Quartier Kandi Centre, Kandi', 'Colis de produits d''hygiène', 4.3, 'ARRIVED', now() - interval '56 hours', now() - interval '22 hours'),
('00000000-0000-4000-8000-000000000008', 'TRK-2026-900008', 'Ferme Gbaguidi', '+229 01 94 00 11 08', 'Quartier Zogbadjè, Abomey-Calavi', 'Hounkpatin Dossou', '+229 01 96 00 22 08', 'Quartier Lokossa Centre, Lokossa', 'Sac de 25 kg d''ignames', 25.0, 'IN_TRANSIT', now() - interval '36 hours', now() - interval '21 hours'),
('00000000-0000-4000-8000-000000000009', 'TRK-2026-900009', 'Studio Pixel Cotonou', '+229 01 97 00 11 09', 'Quartier Akpakpa, Cotonou', 'Élodie Sossou', '+229 01 91 00 22 09', 'Quartier Ouidah Centre, Ouidah', 'Appareil photo et trépied', 2.6, 'SHIPPED', now() - interval '20 hours', now() - interval '14 hours'),
('00000000-0000-4000-8000-000000000010', 'TRK-2026-900010', 'Quincaillerie Houénou', '+229 01 95 00 11 10', 'Quartier Zongo, Parakou', 'Ibrahim Tchibozo', '+229 01 94 00 22 10', 'Quartier Tchaourou Centre, Tchaourou', 'Outillage et visserie', 14.0, 'PENDING', now() - interval '8 hours', now() - interval '6 hours'),
('00000000-0000-4000-8000-000000000011', 'TRK-2026-900011', 'Aïcha Moussa', '+229 01 96 00 11 11', 'Quartier Cadjehoun, Cotonou', 'Désiré Gbaguidi', '+229 01 97 00 22 11', 'Quartier Ouando, Porto-Novo', 'Cadeau d''anniversaire', 0.8, 'REGISTERED', now() - interval '1 hours', now() - interval '1 hours')
ON CONFLICT (id) DO NOTHING;

INSERT INTO tracking_events (id, shipment_id, previous_status, new_status, location, description, created_at) VALUES
('00000000-0000-4000-9000-000000000101', '00000000-0000-4000-8000-000000000001', NULL, 'REGISTERED', 'Cotonou', 'Colis enregistré dans TRACK', now() - interval '288 hours'),
('00000000-0000-4000-9000-000000000102', '00000000-0000-4000-8000-000000000001', 'REGISTERED', 'PENDING', 'Cotonou', 'En attente de paiement', now() - interval '268 hours'),
('00000000-0000-4000-9000-000000000103', '00000000-0000-4000-8000-000000000001', 'PENDING', 'CANCELLED', 'Cotonou', 'Envoi annulé à la demande de l''expéditeur', now() - interval '236 hours'),
('00000000-0000-4000-9000-000000000201', '00000000-0000-4000-8000-000000000002', NULL, 'REGISTERED', 'Porto-Novo', 'Colis enregistré dans TRACK', now() - interval '240 hours'),
('00000000-0000-4000-9000-000000000202', '00000000-0000-4000-8000-000000000002', 'REGISTERED', 'SHIPPED', 'Porto-Novo', 'Colis remis au transporteur', now() - interval '234 hours'),
('00000000-0000-4000-9000-000000000203', '00000000-0000-4000-8000-000000000002', 'SHIPPED', 'IN_TRANSIT', 'Bohicon', 'Passage au centre de tri', now() - interval '220 hours'),
('00000000-0000-4000-9000-000000000204', '00000000-0000-4000-8000-000000000002', 'IN_TRANSIT', 'ARRIVED', 'Natitingou', 'Arrivé à l''agence de destination', now() - interval '196 hours'),
('00000000-0000-4000-9000-000000000205', '00000000-0000-4000-8000-000000000002', 'ARRIVED', 'OUT_FOR_DELIVERY', 'Natitingou', 'Pris en charge par le livreur', now() - interval '186 hours'),
('00000000-0000-4000-9000-000000000206', '00000000-0000-4000-8000-000000000002', 'OUT_FOR_DELIVERY', 'DELIVERY_FAILED', 'Natitingou', 'Destinataire absent, nouvelle tentative prévue', now() - interval '182 hours'),
('00000000-0000-4000-9000-000000000207', '00000000-0000-4000-8000-000000000002', 'DELIVERY_FAILED', 'RETURNED', 'Porto-Novo', 'Colis retourné à l''expéditeur', now() - interval '120 hours'),
('00000000-0000-4000-9000-000000000301', '00000000-0000-4000-8000-000000000003', NULL, 'REGISTERED', 'Cotonou', 'Colis enregistré dans TRACK', now() - interval '200 hours'),
('00000000-0000-4000-9000-000000000302', '00000000-0000-4000-8000-000000000003', 'REGISTERED', 'SHIPPED', 'Cotonou', 'Colis remis au transporteur', now() - interval '195 hours'),
('00000000-0000-4000-9000-000000000303', '00000000-0000-4000-8000-000000000003', 'SHIPPED', 'IN_TRANSIT', 'Dassa-Zoumè', 'Passage au centre de tri', now() - interval '180 hours'),
('00000000-0000-4000-9000-000000000304', '00000000-0000-4000-8000-000000000003', 'IN_TRANSIT', 'ARRIVED', 'Parakou', 'Arrivé à l''agence de destination', now() - interval '164 hours'),
('00000000-0000-4000-9000-000000000305', '00000000-0000-4000-8000-000000000003', 'ARRIVED', 'OUT_FOR_DELIVERY', 'Parakou', 'Pris en charge par le livreur', now() - interval '154 hours'),
('00000000-0000-4000-9000-000000000306', '00000000-0000-4000-8000-000000000003', 'OUT_FOR_DELIVERY', 'DELIVERY_FAILED', 'Parakou', 'Adresse introuvable, contacter le destinataire', now() - interval '150 hours'),
('00000000-0000-4000-9000-000000000401', '00000000-0000-4000-8000-000000000004', NULL, 'REGISTERED', 'Parakou', 'Colis enregistré dans TRACK', now() - interval '150 hours'),
('00000000-0000-4000-9000-000000000402', '00000000-0000-4000-8000-000000000004', 'REGISTERED', 'SHIPPED', 'Parakou', 'Colis remis au transporteur', now() - interval '146 hours'),
('00000000-0000-4000-9000-000000000403', '00000000-0000-4000-8000-000000000004', 'SHIPPED', 'IN_TRANSIT', 'Savè', 'Passage au centre de tri', now() - interval '134 hours'),
('00000000-0000-4000-9000-000000000404', '00000000-0000-4000-8000-000000000004', 'IN_TRANSIT', 'ARRIVED', 'Cotonou', 'Arrivé à l''agence de destination', now() - interval '116 hours'),
('00000000-0000-4000-9000-000000000405', '00000000-0000-4000-8000-000000000004', 'ARRIVED', 'OUT_FOR_DELIVERY', 'Cotonou', 'Pris en charge par le livreur', now() - interval '108 hours'),
('00000000-0000-4000-9000-000000000406', '00000000-0000-4000-8000-000000000004', 'OUT_FOR_DELIVERY', 'DELIVERED', 'Cotonou', 'Colis remis au destinataire', now() - interval '104 hours'),
('00000000-0000-4000-9000-000000000501', '00000000-0000-4000-8000-000000000005', NULL, 'REGISTERED', 'Cotonou', 'Colis enregistré dans TRACK', now() - interval '110 hours'),
('00000000-0000-4000-9000-000000000502', '00000000-0000-4000-8000-000000000005', 'REGISTERED', 'SHIPPED', 'Cotonou', 'Colis remis au transporteur', now() - interval '107 hours'),
('00000000-0000-4000-9000-000000000503', '00000000-0000-4000-8000-000000000005', 'SHIPPED', 'IN_TRANSIT', 'Parakou', 'Passage au centre de tri', now() - interval '88 hours'),
('00000000-0000-4000-9000-000000000504', '00000000-0000-4000-8000-000000000005', 'IN_TRANSIT', 'ARRIVED', 'Djougou', 'Arrivé à l''agence de destination', now() - interval '76 hours'),
('00000000-0000-4000-9000-000000000505', '00000000-0000-4000-8000-000000000005', 'ARRIVED', 'OUT_FOR_DELIVERY', 'Djougou', 'Pris en charge par le livreur', now() - interval '70 hours'),
('00000000-0000-4000-9000-000000000601', '00000000-0000-4000-8000-000000000006', NULL, 'REGISTERED', 'Cotonou', 'Colis enregistré dans TRACK', now() - interval '80 hours'),
('00000000-0000-4000-9000-000000000602', '00000000-0000-4000-8000-000000000006', 'REGISTERED', 'SHIPPED', 'Cotonou', 'Colis remis au transporteur', now() - interval '76 hours'),
('00000000-0000-4000-9000-000000000603', '00000000-0000-4000-8000-000000000006', 'SHIPPED', 'IN_TRANSIT', 'Godomey', 'Passage au centre de tri', now() - interval '68 hours'),
('00000000-0000-4000-9000-000000000604', '00000000-0000-4000-8000-000000000006', 'IN_TRANSIT', 'ARRIVED', 'Abomey-Calavi', 'Arrivé à l''agence de destination', now() - interval '60 hours'),
('00000000-0000-4000-9000-000000000605', '00000000-0000-4000-8000-000000000006', 'ARRIVED', 'READY_FOR_PICKUP', 'Abomey-Calavi', 'Disponible en point relais, à retirer sous 7 jours', now() - interval '54 hours'),
('00000000-0000-4000-9000-000000000701', '00000000-0000-4000-8000-000000000007', NULL, 'REGISTERED', 'Cotonou', 'Colis enregistré dans TRACK', now() - interval '56 hours'),
('00000000-0000-4000-9000-000000000702', '00000000-0000-4000-8000-000000000007', 'REGISTERED', 'SHIPPED', 'Cotonou', 'Colis remis au transporteur', now() - interval '52 hours'),
('00000000-0000-4000-9000-000000000703', '00000000-0000-4000-8000-000000000007', 'SHIPPED', 'IN_TRANSIT', 'Parakou', 'Passage au centre de tri', now() - interval '36 hours'),
('00000000-0000-4000-9000-000000000704', '00000000-0000-4000-8000-000000000007', 'IN_TRANSIT', 'ARRIVED', 'Kandi', 'Arrivé à l''agence de destination', now() - interval '22 hours'),
('00000000-0000-4000-9000-000000000801', '00000000-0000-4000-8000-000000000008', NULL, 'REGISTERED', 'Abomey-Calavi', 'Colis enregistré dans TRACK', now() - interval '36 hours'),
('00000000-0000-4000-9000-000000000802', '00000000-0000-4000-8000-000000000008', 'REGISTERED', 'SHIPPED', 'Abomey-Calavi', 'Colis remis au transporteur', now() - interval '33 hours'),
('00000000-0000-4000-9000-000000000803', '00000000-0000-4000-8000-000000000008', 'SHIPPED', 'IN_TRANSIT', 'Ouidah', 'Passage au centre de tri', now() - interval '21 hours'),
('00000000-0000-4000-9000-000000000901', '00000000-0000-4000-8000-000000000009', NULL, 'REGISTERED', 'Cotonou', 'Colis enregistré dans TRACK', now() - interval '20 hours'),
('00000000-0000-4000-9000-000000000902', '00000000-0000-4000-8000-000000000009', 'REGISTERED', 'SHIPPED', 'Cotonou', 'Colis remis au transporteur', now() - interval '14 hours'),
('00000000-0000-4000-9000-000000001001', '00000000-0000-4000-8000-000000000010', NULL, 'REGISTERED', 'Parakou', 'Colis enregistré dans TRACK', now() - interval '8 hours'),
('00000000-0000-4000-9000-000000001002', '00000000-0000-4000-8000-000000000010', 'REGISTERED', 'PENDING', 'Parakou', 'En attente de collecte par le transporteur', now() - interval '6 hours'),
('00000000-0000-4000-9000-000000001101', '00000000-0000-4000-8000-000000000011', NULL, 'REGISTERED', 'Cotonou', 'Colis enregistré dans TRACK', now() - interval '1 hours')
ON CONFLICT (id) DO NOTHING;

COMMIT;

-- Pour supprimer uniquement ces données de test :
-- DELETE FROM tracking_events WHERE shipment_id IN (SELECT id FROM shipments WHERE tracking_number LIKE 'TRK-2026-9000%');
-- DELETE FROM shipments WHERE tracking_number LIKE 'TRK-2026-9000%';
