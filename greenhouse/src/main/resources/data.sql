
USE `greenhouse_db`;

-- INSERT USERS FIRST
INSERT INTO users (email, password, name, role)
VALUES ('admin@greenhouse.com', '$2a$12$r4B7U7F7G7H7J7K7L7M7N7O7P7Q7R7S7T7U7V7W7X7Y7Z7a7b7c7d7e7f', 'Admin User',
        'ADMIN'),
       ('john@greenhouse.com', '$2a$12$s5C8V8G8H8J8K8L8M8N8O8P8Q8R8S8T8U8V8W8X8Y8Z8a8b8c8d8e8f', 'John Doe', 'USER'),
       ('jane@greenhouse.com', '$2a$12$t6D9W9H9J9K9L9M9N9O9P9Q9R9S9T9U9V9W9X9Y9Z9a9b9c9d9e9f', 'Jane Smith', 'USER');

-- INSERT GREENHOUSES WITH ALL REQUIRED COLUMNS
INSERT INTO greenhouse (id, name, location, capacity, user_id, moisture_threshold, default_watering_amount,
                        auto_watering_enabled)
VALUES (1, 'Greenhouse Alpha', 'Backyard', 5, 2, 40.0, 20.0, true),
       (2, 'Greenhouse Beta', 'Frontyard', 8, 2, 35.0, 25.0, true),
       (3, 'Greenhouse Gamma', 'Rooftop', 4, 3, 45.0, 15.0, true),
       (4, 'Greenhouse Delta', 'Side Garden', 6, 3, 38.0, 22.0, true),
       (5, 'Greenhouse Omega', 'Indoor Lab', 3, 1, 42.0, 18.0, true);

-- SENSORS FOR GREENHOUSE 1 (2 sensors)
INSERT INTO sensor (sensor_type, unit, min_threshold, max_threshold, is_active, greenhouse_id)
VALUES ('TEMPERATURE', '°C', 15.0, 30.0, true, 1),
       ('SOIL_MOISTURE', '%', 30.0, 60.0, true, 1);

-- SENSORS FOR GREENHOUSE 2 (2 sensors)
INSERT INTO sensor (sensor_type, unit, min_threshold, max_threshold, is_active, greenhouse_id)
VALUES ('TEMPERATURE', '°C', 10.0, 35.0, true, 2),
       ('SOIL_MOISTURE', '%', 25.0, 65.0, true, 2);

-- SENSORS FOR GREENHOUSE 3 (2 sensors)
INSERT INTO sensor (sensor_type, unit, min_threshold, max_threshold, is_active, greenhouse_id)
VALUES ('TEMPERATURE', '°C', 12.0, 32.0, true, 3),
       ('SOIL_MOISTURE', '%', 35.0, 70.0, true, 3);

-- SENSORS FOR GREENHOUSE 4 (3 sensors - extra humidity)
INSERT INTO sensor (sensor_type, unit, min_threshold, max_threshold, is_active, greenhouse_id)
VALUES ('TEMPERATURE', '°C', 8.0, 40.0, true, 4),
       ('SOIL_MOISTURE', '%', 20.0, 75.0, true, 4),
       ('HUMIDITY', '%', 40.0, 80.0, true, 4);

-- SENSORS FOR GREENHOUSE 5 (3 sensors - extra light)
INSERT INTO sensor (sensor_type, unit, min_threshold, max_threshold, is_active, greenhouse_id)
VALUES ('TEMPERATURE', '°C', 5.0, 45.0, true, 5),
       ('SOIL_MOISTURE', '%', 15.0, 85.0, true, 5),
       ('LIGHT', 'lux', 200.0, 800.0, true, 5);

-- ADD SOME WATERING LOGS FOR REALISM (using watering_source instead of source)
INSERT INTO watering_log (timestamp, water_amount, watering_source, greenhouse_id)
VALUES (NOW() - INTERVAL 2 DAY, 15.0, 'MANUAL', 1),
       (NOW() - INTERVAL 1 DAY, 20.0, 'AUTO', 1),
       (NOW() - INTERVAL 3 DAY, 18.0, 'MANUAL', 2),
       (NOW() - INTERVAL 1 DAY, 22.0, 'AUTO', 3),
       (NOW() - INTERVAL 4 DAY, 12.0, 'MANUAL', 4),
       (NOW() - INTERVAL 2 DAY, 25.0, 'AUTO', 5);

-- ADD SOME SENSOR READINGS (with greenhouse_id included)
INSERT INTO sensor_readings (value, timestamp, sensor_id)
VALUES (22.5, NOW() - INTERVAL 1 HOUR, 1),
       (45.0, NOW() - INTERVAL 1 HOUR, 2),
       (25.0, NOW() - INTERVAL 1 HOUR, 3),
       (50.0, NOW() - INTERVAL 1 HOUR, 4),
       (20.0, NOW() - INTERVAL 1 HOUR, 5),
       (60.0, NOW() - INTERVAL 1 HOUR, 6),
       (18.0, NOW() - INTERVAL 1 HOUR, 7),
       (55.0, NOW() - INTERVAL 1 HOUR, 8),
       (65.0, NOW() - INTERVAL 1 HOUR, 9),
       (12.0, NOW() - INTERVAL 1 HOUR, 10),
       (70.0, NOW() - INTERVAL 1 HOUR, 11),
       (500.0, NOW() - INTERVAL 1 HOUR, 12);