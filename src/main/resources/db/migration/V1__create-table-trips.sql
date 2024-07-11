-- Active: 1695805259198@@127.0.0.1@5432@planner
CREATE TABLE trips(
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  destination VARCHAR(255) NOT NULL,
  starts_at TIMESTAMP NOT NULL,
  ends_at TIMESTAMP NOT NULL,
  is_confirmed BOOLEAN NOT NULL,
  owner_name VARCHAR(255) NULL NULL,
  owner_email VARCHAR(255) NOT NULL
);