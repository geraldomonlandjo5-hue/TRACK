-- Migration NON DESTRUCTIVE d'une base TRACK existante vers cette version.
-- Sauvegarder la base avant exécution (pg_dump). Peut être relancée sans risque.
-- Ensuite : exécuter seed-admin.sql si aucun compte administrateur n'existe encore.

CREATE TABLE IF NOT EXISTS agencies (
    id VARCHAR(36) PRIMARY KEY, code VARCHAR(20) NOT NULL UNIQUE, name VARCHAR(120) NOT NULL,
    city VARCHAR(100) NOT NULL, address VARCHAR(255), phone VARCHAR(30), email VARCHAR(150),
    is_active BOOLEAN NOT NULL DEFAULT TRUE, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS roles (
    id VARCHAR(36) PRIMARY KEY, name VARCHAR(50) NOT NULL UNIQUE, description VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY, agency_id VARCHAR(36) REFERENCES agencies(id),
    role_id VARCHAR(36) NOT NULL REFERENCES roles(id),
    full_name VARCHAR(120) NOT NULL, email VARCHAR(150) NOT NULL UNIQUE, phone VARCHAR(30),
    password_hash VARCHAR(255) NOT NULL, is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_users_email ON users (lower(email));
CREATE UNIQUE INDEX IF NOT EXISTS uq_users_email_lower ON users (lower(email));

-- Nouvelles colonnes (rattachement des colis à une agence, visibilité client, audit)
ALTER TABLE shipments ADD COLUMN IF NOT EXISTS agency_id VARCHAR(36) REFERENCES agencies(id);
ALTER TABLE tracking_events ADD COLUMN IF NOT EXISTS is_public BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE tracking_events ADD COLUMN IF NOT EXISTS created_by VARCHAR(36) REFERENCES users(id);

CREATE INDEX IF NOT EXISTS idx_shipments_agency ON shipments(agency_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_shipments_created_at ON shipments(created_at DESC);

-- ATTENTION : les colis existants ont agency_id = NULL, donc seuls SUPER_ADMIN et ADMIN les voient.
-- Pour les rattacher à une agence : UPDATE shipments SET agency_id = '<id agence>' WHERE ... ;
