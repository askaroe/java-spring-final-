-- Create one database per service and grant privileges to user 'app'
-- This runs on first startup of Postgres in the container

CREATE DATABASE api_gateway_db;
CREATE DATABASE booking_db;
CREATE DATABASE event_db;
CREATE DATABASE user_db;

-- Grant privileges to the 'app' user
GRANT ALL PRIVILEGES ON DATABASE api_gateway_db TO app;
GRANT ALL PRIVILEGES ON DATABASE booking_db TO app;
GRANT ALL PRIVILEGES ON DATABASE event_db TO app;
GRANT ALL PRIVILEGES ON DATABASE user_db TO app;

