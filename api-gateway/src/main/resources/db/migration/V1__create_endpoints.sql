-- Create endpoints table for dynamic gateway routing and RBAC
CREATE TABLE IF NOT EXISTS endpoints (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    host VARCHAR(255) NOT NULL,
    service VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    method VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

