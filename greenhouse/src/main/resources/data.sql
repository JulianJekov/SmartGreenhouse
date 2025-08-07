USE `greenhouse_db`;

INSERT INTO greenhouse (id, name, location, capacity) VALUES (1, 'Greenhouse Alpha', 'Backyard', 5);
INSERT INTO greenhouse (id, name, location, capacity) VALUES (2, 'Greenhouse Beta', 'Frontyard', 8);
INSERT INTO greenhouse (id, name, location, capacity) VALUES (3, 'Greenhouse Gamma', 'Rooftop', 4);
INSERT INTO greenhouse (id, name, location, capacity) VALUES (4, 'Greenhouse Delta', 'Side Garden', 6);
INSERT INTO greenhouse (id, name, location, capacity) VALUES (5, 'Greenhouse Omega', 'Indoor Lab', 3);

-- SENSORS
INSERT INTO sensor (sensor_type, unit, min_threshold, max_threshold, is_active, greenhouse_id)
VALUES ('TEMPERATURE', '°C', 10.0, 35.0, true, 1);

INSERT INTO sensor (sensor_type, unit, min_threshold, max_threshold, is_active, greenhouse_id)
VALUES ('HUMIDITY', '%', 40.0, 80.0, true, 2);

INSERT INTO sensor (sensor_type, unit, min_threshold, max_threshold, is_active, greenhouse_id)
VALUES ('SOIL_MOISTURE', '%', 30.0, 60.0, false, 3);

INSERT INTO sensor (sensor_type, unit, min_threshold, max_threshold, is_active, greenhouse_id)
VALUES ('LIGHT', 'lux', 200.0, 800.0, true, 4);

INSERT INTO sensor (sensor_type, unit, min_threshold, max_threshold, is_active, greenhouse_id)
VALUES ('TEMPERATURE', '°C', 5.0, 45.0, true, 5);