-- Initialize database for Candy Book Store
CREATE DATABASE IF NOT EXISTS candydb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user if not exists
CREATE USER IF NOT EXISTS 'candyuser'@'%' IDENTIFIED BY 'candypass';

-- Grant privileges
GRANT ALL PRIVILEGES ON candydb.* TO 'candyuser'@'%';
GRANT ALL PRIVILEGES ON candydb.* TO 'root'@'%';

-- Flush privileges
FLUSH PRIVILEGES;

-- Use the database
USE candydb;

-- Optional: Create some initial setup if needed
-- This will be executed when the MySQL container starts for the first time
