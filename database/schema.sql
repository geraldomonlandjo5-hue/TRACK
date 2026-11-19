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

CREATE INDEX IF NOT EXISTS idx_tracking_events_shipment
    ON tracking_events(shipment_id, created_at);
