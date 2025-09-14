# 🌱 Smart Greenhouse API

> Manage your greenhouses, sensors, and watering – all in one smart system.  
> Secure. Scalable. Personal.  

![Java](https://img.shields.io/badge/Java-17+-blue)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-green)  
![JWT](https://img.shields.io/badge/Security-JWT-orange)  
![MySQL](https://img.shields.io/badge/Database-MySQL-lightblue)  
![Status](https://img.shields.io/badge/Status-Active%20Development-brightgreen)

---

✨ Features

✅ JWT Authentication with refresh & logout
✅ OAuth2 Login (Google, GitHub, etc.)
✅ User-based access – each user sees only their greenhouses
✅ Greenhouse & Sensor Management
✅ Automatic & Manual Watering with logs
✅ Sensor Readings (latest + historical)
✅ Email Verification
✅ Forgot Password Flow
✅ Change Password (secure flow with old password check)
✅ Notifications – email alerts on watering events (manual or automatic)
✅ Error Handling & Custom Exceptions
🚀 Planned: MQTT Integration + Microservices migration
---

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 3, Spring Security, Spring Data JPA  
- **Database**: MySQL + Hibernate  
- **Security**: JWT (access & refresh tokens)  
- **Email**: Gmail SMTP + Thymeleaf templates  
- **Other**: Lombok, Validation API, Postman for testing  

---

## 📚 API Overview

### 👤 User Management
- Register, Login, Logout, Refresh tokens  
- Email verification (via link)  
- Forgot & Reset password flow
- Change password (authenticated users)
- OAuth2 login (Google, GitHub, etc.)
- Get current user info  

### 🌿 Greenhouses
- CRUD operations  
- Auto-watering toggle  
- Sensor overview per greenhouse  

### 📡 Sensors
- CRUD sensors  
- Sensor statistics  

### 📊 Sensor Readings
- Latest reading  
- History in date range  
- Simulated data creation  

### 💧 Watering
- Manual watering  
- Automatic watering (scheduled)  
- Watering logs
- Email notifications after watering events

---

## 🚀 Quick Start

```bash
# clone
git clone https://github.com/username/smart-greenhouse.git

# run
mvn spring-boot:run

Configure your application.yml with DB and email settings:

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/greenhouse
    username: root
    password: yourpassword

mail:
  host: smtp.gmail.com
  port: 587
  username: smartgreenhouse.app@gmail.com
  password: ${EMAIL_PASSWORD}


---

📌 Roadmap

[x] JWT Authentication

[x] OAuth2 Login

[x] User-based resource access

[x] Greenhouse + Sensor CRUD

[x] Email Verification

[x] Forgot Password

[x] Change Password

[x] Notifications (watering events)

[ ] MQTT integration for real sensor data

[ ] Swagger/OpenAPI docs

[ ] Microservices
