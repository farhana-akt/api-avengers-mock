-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'CUSTOMER',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);

-- Create index on role
CREATE INDEX idx_users_role ON users(role);

-- Insert default admin user (password: admin123)
INSERT INTO users (email, password, first_name, last_name, role, active)
VALUES ('admin@ecommerce.com', '$2a$10$8JcR8qYZX/jYO/0vZR8Y4.KX9vX0yJZX9vX0yJZX9vX0yJZX9vX0y', 'Admin', 'User', 'ADMIN', true);

-- Insert test customer user (password: customer123)
INSERT INTO users (email, password, first_name, last_name, role, active)
VALUES ('customer@test.com', '$2a$10$8JcR8qYZX/jYO/0vZR8Y4.KX9vX0yJZX9vX0yJZX9vX0yJZX9vX0y', 'Test', 'Customer', 'CUSTOMER', true);
