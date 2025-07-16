CREATE DATABASE IF NOT EXISTS candydb;

-- Connect to the candydb database
\c candydb;

-- Create a schema for our application (optional)
CREATE SCHEMA IF NOT EXISTS candy;

-- Grant privileges to the postgres user
GRANT ALL PRIVILEGES ON DATABASE candydb TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA candy TO postgres;
