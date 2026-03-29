# SkillSync Microservices Architecture Overview

This document provides a comprehensive breakdown of the SkillSync microservices ecosystem, explaining the role of each service, their endpoints, and how they interact using **OpenFeign** (Synchronous) and **RabbitMQ** (Asynchronous).

---

## 🏗️ System Architecture

SkillSync follows a modern microservices architecture where each service is responsible for a specific domain. They are loosely coupled and communicate through well-defined interfaces.

### 1. Communication Patterns

| Pattern | Technology | Purpose | Example |
| :--- | :--- | :--- | :--- |
| **Synchronous** | **OpenFeign** | Immediate request-response between services. | `session-service` calls `user-service` to get learner details. |
| **Asynchronous** | **RabbitMQ** | Event-driven notifications (Fire-and-forget). | `session-service` emits a `SESSION_BOOKED` event. |

---

## 🛠️ Microservice Breakdown

### Infrastructure Services
| Service | Role | Description |
| :--- | :--- | :--- |
| **Eureka Server** | Service Registry | Acts as a phonebook for all services, allowing them to find each other. |
| **Config Server** | Centralized Config | Manages configuration files (YAML/Properties) for all services in one place. |
| **API Gateway** | Entry Point | Routes all external requests to the correct service and handles cross-cutting concerns (Security, CORS). |

### Core Domain Services
| Service | Role | What / Why / How / Where |
| :--- | :--- | :--- |
| **Auth Service** | Identity & Security | Handles JWT generation, login, and registration. It ensures only authorized users can access the system. |
| **User Service** | Profile Management | Manages user profiles (Bio, Name, Contact). Used by other services to fetch user data via Feign. |
| **Mentor Service** | Mentor Onboarding | Handles mentor applications and approvals. It maintains the list of specialized mentors. |
| **Skill Service** | Skill Taxonomy | Defines the categories and specific skills available for mentoring (e.g., Java, React). |

### Interaction Services
| Service | Role | What / Why / How / Where |
| :--- | :--- | :--- |
| **Session Service** | Booking Logic | The core of the app. Handles booking requests, scheduling, and session status (Accepted/Rejected). |
| **Group Service** | Community | Allows users to create groups for collaborative learning based on shared interests/skills. |
| **Review Service** | Feedback Loop | Collects ratings and reviews for mentors after sessions are completed. |

### Supporting Services
| Service | Role | What / Why / How / Where |
| :--- | :--- | :--- |
| **Notification** | Communication | Listens to RabbitMQ events and sends logs (or emails) to users about system updates. |

---

## 📡 Service Endpoints & Abilities

| Service | Endpoint Prefix | Key Abilities |
| :--- | :--- | :--- |
| **Auth** | `/auth` | Register, Login, Token Validation. |
| **User** | `/users` | Get Profile, Update Profile, Get User by ID. |
| **Mentor** | `/mentors` | Apply to be mentor, Approve application, List mentors. |
| **Skill** | `/skills` | Add skill, List skills, Get skill details. |
| **Session** | `/sessions` | Book session, Accept/Reject session, Get session history. |
| **Group** | `/groups` | Create group, Join group, Search groups. |
| **Review** | `/reviews` | Post review, Get mentor ratings, List reviews. |

---

## 🔗 The Linkage (Feign & Rabbit)

### 🧩 OpenFeign (The "Need to Know Now" link)
This is used when a service **needs data** from another service to complete its own operation.
- **Example**: When you book a session, the `session-service` uses Feign to call `user-service` to verify the learner exists and `mentor-service` to verify the mentor is active.

### ✉️ RabbitMQ (The "Tell Everyone Later" link)
This is used to **notify** other parts of the system that something happened.
- **Example**: When a mentor rejects a session:
    1. `session-service` updates the database.
    2. `session-service` sends a `SESSION_REJECTED` message to the `Exchange`.
    3. **RabbitMQ** routes this message to the `notification.main.queue`.
    4. **Notification Service** consumes the message and logs/sends an email.

---

## 🔔 The Notification Service: Deep Dive

The **Notification Service** acts as an event subscriber. It waits for specific "signals" from other services.

### What does it perform?
When `mentor-service` or `session-service` sends a message to RabbitMQ:
1. **Event Reception**: It receives a JSON payload containing `eventType`, `userId`, `sessionId`, etc.
2. **Logic Execution**: It identifies the type (e.g., `MENTOR_APPROVED`).
3. **Action**: In the current implementation, it logs the event for audit purposes. In a production scenario, it interfaces with an **Email Service** (like SMTP or SendGrid) to send actual emails.

### Triggers & Notifications:
| Event | Triggered By | Recipient | Content |
| :--- | :--- | :--- | :--- |
| `MENTOR_APPROVED` | `mentor-service` | User | "Congratulations! You are now a verified mentor." |
| `SESSION_BOOKED` | `session-service` | Mentor | "A learner has requested a session with you." |
| `SESSION_ACCEPTED` | `session-service` | Learner | "Your session request has been accepted by the mentor." |
| `SESSION_REJECTED` | `session-service` | Learner | "Sorry, the mentor is unavailable. Your session was rejected." |

---

> [!NOTE]
> All inter-service communication via Feign is protected by JWT headers passed through the `api-gateway`.
