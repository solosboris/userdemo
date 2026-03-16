-- Create users table in user_management schema
SET search_path TO user_management;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL CHECK (LENGTH(first_name) >= 2),
    last_name VARCHAR(50) NOT NULL CHECK (LENGTH(last_name) >= 2),
    email VARCHAR(255) UNIQUE NOT NULL CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    phone VARCHAR(15),
    address TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Insert sample data (optional)
INSERT INTO users (first_name, last_name, email, phone, address, status) VALUES
('John', 'Doe', 'john.doe@example.com', '+1234567890', '123 Main St, City, Country', 'ACTIVE'),
('Jane', 'Smith', 'jane.smith@example.com', '+1987654321', '456 Oak St, Another City', 'ACTIVE'),
('Admin', 'User', 'admin@userdemo.com', '+1111111111', 'Admin Address', 'ACTIVE')
ON CONFLICT (email) DO NOTHING;

-- Grant permissions to appuser
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA user_management TO appuser;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA user_management TO appuser;