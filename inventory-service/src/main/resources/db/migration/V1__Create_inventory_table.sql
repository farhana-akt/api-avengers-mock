-- Create inventory table
CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    available_quantity INTEGER NOT NULL DEFAULT 0,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on product_id for faster lookups
CREATE INDEX idx_inventory_product_id ON inventory(product_id);

-- Insert sample inventory data (matching products from product service)
INSERT INTO inventory (product_id, available_quantity, reserved_quantity) VALUES
(1, 50, 0),   -- Laptop Pro 15
(2, 200, 0),  -- Wireless Mouse
(3, 75, 0),   -- Mechanical Keyboard
(4, 150, 0),  -- Running Shoes
(5, 100, 0),  -- Yoga Mat
(6, 80, 0),   -- Coffee Maker
(7, 120, 0),  -- Blender
(8, 90, 0),   -- Backpack
(9, 300, 0),  -- Water Bottle
(10, 60, 0);  -- Headphones
