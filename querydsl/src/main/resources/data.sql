-- Insert teams into the teams table
INSERT INTO teams (name) VALUES ('Engineering'), ('Marketing'), ('Sales');

INSERT INTO users (email, password, name, age, created_at, team_id)
VALUES
    ('john.doe@example.com', 'password123', 'John Doe', 25, '2024-08-21 10:00:00', 1),  -- Assuming team_id 1 is 'Engineering'
    ('jane.doe@example.com', 'password456', 'Jane Doe', 30, '2024-08-20 11:30:00', 2),  -- Assuming team_id 2 is 'Marketing'
    ('alice.smith@example.com', 'password789', 'Alice Smith', 22, '2024-08-19 09:15:00', 1),  -- Also 'Engineering'
    ('bob.johnson@example.com', 'password000', 'Bob Johnson', 28, '2024-08-18 08:45:00', 3); -- Assuming team_id 3 is 'Sales'
