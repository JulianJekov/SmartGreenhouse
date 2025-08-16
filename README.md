# 🌱 Smart Greenhouse (IoT Simulation with Spring Boot)

## Overview
The **Smart Greenhouse** project is a Spring Boot application that simulates the core logic of an IoT-based greenhouse automation system.  
It manages greenhouses, moisture sensors, and watering logic.  
The goal is to provide a realistic backend system that can later integrate with real hardware such as **ESP32 controllers** and **MQTT brokers**.

At this stage, the project runs in **simulation mode** using scheduled tasks and random sensor values. Future iterations will replace these simulations with actual IoT devices.

---

## ✅ Current Functionality

### Greenhouse Management
- Each greenhouse has:
  - Moisture sensor (simulated)
  - Auto-watering threshold
  - Default watering amount
  - Enabled/disabled auto-watering

### Watering Logic
- **Manual watering** (`POST /api/watering/manual`)
- **Automatic watering** (scheduled checks every minute)
  - Reads simulated sensor values
  - Compares against greenhouse threshold
  - Triggers watering if moisture is too low
- **Watering logs** (`WateringLog` entity) keep history:
  - Amount
  - Source (AUTO or MANUAL)
  - Timestamp

### Sensor Simulation
- `SimulatedSensorReader` generates random values between sensor’s thresholds.
- Scheduler periodically checks moisture levels.

### Error Handling
- If watering actuator fails, a `WateringFailedException` is thrown.
- Retry mechanism: watering is attempted up to **3 times** before failure is logged.

---

## 🚀 Planned Features

### A. Validation & Protection
- Prevent manual watering if auto-watering is currently active (lock mechanism).
- Validate that a greenhouse with auto-watering enabled must have an active moisture sensor.

### B. History & Statistics
- Store periodic sensor readings in a new `SensorReading` table.
- REST endpoints for:
  - Moisture history (e.g. last 24 hours)
  - Watering history with filters (date, source)

### C. Multi-user Support
- Assign each greenhouse an **owner** (`User` entity).
- Ensure API requests only allow access to the owner’s greenhouses.

### D. IoT Integration
The project is designed to integrate with **real hardware**:
- **ESP32 microcontrollers** connected to:
  - Moisture sensors
  - Pumps/valves
- **MQTT broker** (e.g. Mosquitto) for communication:
  - ESP32 → publishes sensor readings (`/greenhouse/{id}/sensor/moisture`)
  - Backend → subscribes and saves data
  - Backend → publishes actuator commands (`/greenhouse/{id}/actuator/watering`)
  - ESP32 → subscribes and executes watering
- Until real devices are connected, all readings and actuations are simulated in Java.

### E. Frontend Integration
- Planned REST API for a **Vue.js / React frontend**:
  - Dashboard for real-time sensor readings
  - History graphs (moisture, watering)
  - Manual watering controls

---

## 🛠️ Tech Stack
- **Backend:** Java 17, Spring Boot
- **Database:** MySQL
- **ORM:** Hibernate / Spring Data JPA
- **IoT Simulation:** Scheduled tasks + random values
- **Planned IoT Integration:** MQTT (Eclipse Paho), ESP32 devices
- **Build Tool:** Maven

---

## 📦 Installation & Running

1. Clone the repository:
   ```bash
   git clone https://github.com/JulianJekov/SmartGreenhouse.git
