-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(100) NOT NULL,
    brand VARCHAR(100),
    image_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_brand ON products(brand);
CREATE INDEX idx_products_active ON products(active);
CREATE INDEX idx_products_name ON products(name);

-- Insert sample products
INSERT INTO products (name, description, price, category, brand, image_url, active) VALUES
('Laptop Pro 15', 'High-performance laptop with 16GB RAM and 512GB SSD', 1299.99, 'Electronics', 'TechBrand', 'https://example.com/laptop.jpg', true),
('Wireless Mouse', 'Ergonomic wireless mouse with precision tracking', 29.99, 'Electronics', 'TechBrand', 'https://example.com/mouse.jpg', true),
('Mechanical Keyboard', 'RGB mechanical keyboard with cherry switches', 149.99, 'Electronics', 'GameGear', 'https://example.com/keyboard.jpg', true),
('Running Shoes', 'Comfortable running shoes with excellent cushioning', 89.99, 'Sports', 'SportPro', 'https://example.com/shoes.jpg', true),
('Yoga Mat', 'Premium non-slip yoga mat', 39.99, 'Sports', 'FitLife', 'https://example.com/yogamat.jpg', true),
('Coffee Maker', 'Programmable coffee maker with 12-cup capacity', 79.99, 'Home', 'KitchenPro', 'https://example.com/coffee.jpg', true),
('Blender', 'High-speed blender for smoothies and more', 59.99, 'Home', 'KitchenPro', 'https://example.com/blender.jpg', true),
('Backpack', 'Durable backpack with laptop compartment', 49.99, 'Accessories', 'TravelGear', 'https://example.com/backpack.jpg', true),
('Water Bottle', 'Insulated water bottle keeps drinks cold for 24 hours', 24.99, 'Sports', 'HydroMax', 'https://example.com/bottle.jpg', true),
('Headphones', 'Noise-cancelling wireless headphones', 199.99, 'Electronics', 'AudioTech', 'https://example.com/headphones.jpg', true);
