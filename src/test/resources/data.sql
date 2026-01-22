-- Insert test user
INSERT INTO users (id, username, password, phone_number, balance, active)
VALUES ('f3e02ce0-365d-4c03-90a1-98f00cf6d3d1', 'testuser', 'password123', '1234567890', 100.0, TRUE);

-- Assign role
INSERT INTO user_roles (user_id, roles)
VALUES ('f3e02ce0-365d-4c03-90a1-98f00cf6d3d1', 'USER');

-- Insert voucher for this user
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date,
                     eviction_date, user_id, is_hot)
VALUES ('11111111-1111-1111-1111-111111111111',
        'Amazing Tour',
        'Description of amazing tour',
        299.99,
        'ADVENTURE',
        'BUS',
        'FOUR_STARS',
        'REGISTERED',
        '2025-04-01',
        '2025-04-10',
        'f3e02ce0-365d-4c03-90a1-98f00cf6d3d1',
        TRUE);
