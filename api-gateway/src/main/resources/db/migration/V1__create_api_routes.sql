CREATE TABLE api_routes (
                            id UUID PRIMARY KEY,
                            path_prefix VARCHAR(255) NOT NULL,
                            http_method VARCHAR(20) NOT NULL, -- GET, POST, PUT, DELETE, * etc.
                            target_base_url VARCHAR(255) NOT NULL, -- e.g. http://localhost:8081
                            required_role VARCHAR(50),            -- e.g. ADMIN, USER; NULL = no role required
                            created_at TIMESTAMP DEFAULT NOW(),
                            updated_at TIMESTAMP DEFAULT NOW()
);