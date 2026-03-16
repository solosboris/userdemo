-- Initialize database and create additional user if needed
CREATE USER appuser WITH PASSWORD 'apppassword';
GRANT CONNECT ON DATABASE userdb TO appuser;

-- Create schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS user_management;
GRANT USAGE ON SCHEMA user_management TO appuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA user_management TO appuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA user_management TO appuser;

-- Set default schema
ALTER ROLE appuser SET search_path TO user_management;