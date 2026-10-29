-- Digital Queue Management System
-- Run this script in MySQL Workbench or the MySQL command line.

CREATE DATABASE IF NOT EXISTS digital_queue
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE digital_queue;

CREATE TABLE IF NOT EXISTS token_counters (
    service_type VARCHAR(80) NOT NULL,
    queue_date DATE NOT NULL,
    last_number INT NOT NULL DEFAULT 0,
    PRIMARY KEY (service_type, queue_date)
);

CREATE TABLE IF NOT EXISTS queue_tickets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    service_type VARCHAR(80) NOT NULL,
    token_number INT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    queue_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    called_at TIMESTAMP NULL DEFAULT NULL,
    served_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_service_date_token (service_type, queue_date, token_number),
    KEY idx_queue_service_status (service_type, queue_date, status, token_number)
);

-- Optional: inspect tokens for the current date.
-- SELECT * FROM queue_tickets WHERE queue_date = CURRENT_DATE ORDER BY service_type, token_number;
