-- TRACK : schéma complet (source de vérité). Idempotent : peut être relancé sans risque.
-- Ordre d'exécution : schema.sql -> seed-admin.sql (-> seed-test-benin.sql -> seed-test-agences.sql en test)
-- Base existante d'une version précédente : exécuter migration-core.sql à la place.

CREATE TABLE IF NOT EXISTS agencies (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(30),
    email VARCHAR(150),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    agency_id VARCHAR(36) REFERENCES agencies(id),
    role_id VARCHAR(36) NOT NULL REFERENCES roles(id),
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(30),
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_users_email ON users (lower(email));
CREATE UNIQUE INDEX IF NOT EXISTS uq_users_email_lower ON users (lower(email));

CREATE TABLE IF NOT EXISTS shipments (
    id VARCHAR(36) PRIMARY KEY,
    agency_id VARCHAR(36) REFERENCES agencies(id),       -- agence propriétaire (NULL = administration centrale)
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
CREATE INDEX IF NOT EXISTS idx_shipments_agency ON shipments(agency_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_shipments_created_at ON shipments(created_at DESC);

CREATE TABLE IF NOT EXISTS tracking_events (
    id VARCHAR(36) PRIMARY KEY,
    shipment_id VARCHAR(36) NOT NULL REFERENCES shipments(id),
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    location VARCHAR(180),
    description VARCHAR(500),
    is_public BOOLEAN NOT NULL DEFAULT TRUE,              -- FALSE = note interne, masquée au client
    created_by VARCHAR(36) REFERENCES users(id),          -- auteur de l'événement (audit)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_tracking_events_shipment ON tracking_events(shipment_id, created_at);
