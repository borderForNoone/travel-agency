CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    phone_number VARCHAR(255),
    balance DOUBLE,
    active BOOLEAN
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id VARCHAR(36) NOT NULL,
    roles VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS vouchers (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price DOUBLE,
    tour_type VARCHAR(50),
    transfer_type VARCHAR(50),
    hotel_type VARCHAR(50),
    status VARCHAR(50),
    arrival_date DATE,
    eviction_date DATE,
    user_id VARCHAR(36),
    is_hot BOOLEAN,
    CONSTRAINT fk_voucher_user FOREIGN KEY (user_id) REFERENCES users(id)
);
