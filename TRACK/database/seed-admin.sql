-- À exécuter une seule fois après schema.sql (idempotent : ON CONFLICT DO NOTHING).
-- Compte initial : admin@track.local / ChangeMe2026!
-- Changer ce mot de passe IMMÉDIATEMENT : générer un hash avec
--   java -cp target/classes com.track.security.PasswordTool "NouveauMotDePasse"
-- puis : UPDATE users SET password_hash = '<hash>' WHERE email = 'admin@track.local';
-- Ne pas exécuter ce script (ni les seed-test-*) sur un serveur de production sans changer ce mot de passe.
INSERT INTO roles (id, name, description) VALUES
('00000000-0000-0000-0000-000000000001', 'SUPER_ADMIN', 'Administration centrale'),
('00000000-0000-0000-0000-000000000002', 'ADMIN', 'Administration'),
('00000000-0000-0000-0000-000000000003', 'RESPONSABLE_AGENCE', 'Gestion d’une agence'),
('00000000-0000-0000-0000-000000000004', 'AGENT', 'Opérations courantes'),
('00000000-0000-0000-0000-000000000005', 'LIVREUR', 'Livraison des colis')
ON CONFLICT (name) DO NOTHING;

INSERT INTO agencies (id, code, name, city, address)
VALUES ('00000000-0000-0000-0000-000000000010', 'CENTRAL', 'Administration centrale', 'Cotonou', NULL)
ON CONFLICT (code) DO NOTHING;

INSERT INTO users (id, agency_id, role_id, full_name, email, password_hash, is_active)
SELECT '00000000-0000-0000-0000-000000000020', NULL, r.id,
       'Administrateur TRACK', 'admin@track.local',
       'pbkdf2_sha256$210000$6a24f3a6426748f3db0e6c7a35a6cbf1$7fd3e27e1bb2afaf85221891f806d67f8974f8f5311360f5c3c3dd78301dcba0', TRUE
FROM roles r WHERE r.name = 'SUPER_ADMIN'
ON CONFLICT (email) DO NOTHING;
