CREATE TABLE participants(
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  is_confirmed BOOLEAN NOT NULL,
  trip_id UUID,
  FOREIGN KEY (trip_id) REFERENCES trips(id)
);