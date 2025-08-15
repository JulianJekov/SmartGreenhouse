# Smart Greenhouse

Smart Greenhouse is a Spring Boot application that simulates and manages basic watering operations in a greenhouse environment.  
It supports both **manual watering** triggered by API requests and **automatic watering** triggered by scheduled checks of simulated moisture sensors.

## Overview

The system manages one or more greenhouses stored in a database.  
Each greenhouse can be configured with:

- A moisture threshold value.
- An optional simulated moisture sensor.
- Auto-watering settings (enabled or disabled).
- A default watering amount.

Watering actions are logged in a `WateringLog` table, recording the date, amount, and watering source (manual or automatic).

## Features

- **Manual watering** via REST endpoint.
- **Automatic watering** using a scheduler that periodically checks moisture sensor values.
- **Validation** to ensure a greenhouse exists before watering.
- **Error handling** for failed watering operations.
- **Logging of watering history** in the database.

## Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Data JPA** (Hibernate)
- **H2 in-memory database** (for development)
- **Maven**

## Project Structure

- `controller` – REST controllers for API endpoints.
- `service` – Business logic for watering operations.
- `repository` – Data access layer for entities.
- `entity` – JPA entities such as `Greenhouse` and `WateringLog`.
- `enums` – Enum types like `WateringSource`.
- `exceptions` – Custom exception classes.
- `sensors` – Simulated sensor reading logic.

## API Endpoints

### Manual Watering
