CREATE TABLE predictions (
 id SERIAL PRIMARY KEY,
 landmark VARCHAR(50),
 probability NUMERIC,
 image VARCHAR(255),
 user_id INTEGER REFERENCES users(id) ON DELETE CASCADE
);