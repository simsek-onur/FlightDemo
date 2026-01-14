CREATE SEQUENCE IF NOT EXISTS seq_flight_leg
  INCREMENT BY 1
  START WITH 1
  MINVALUE 1
  CACHE 1;

CREATE TABLE IF NOT EXISTS flight (
                                      id BIGINT PRIMARY KEY DEFAULT nextval('seq_flight_leg'),
    flight_id VARCHAR(64),
    carrier_code VARCHAR(8),
    flight_number BIGINT,
    flight_date DATE,
    traffic VARCHAR(16),
    departure_airport VARCHAR(8),
    arrival_airport VARCHAR(8),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_flight_flight_id ON flight (flight_id);