# Reservation & Inventory System

A full-stack reservation and inventory management app built with **React, TypeScript, Spring Boot, and PostgreSQL**.

The main engineering focus is preventing **overselling / double booking** using database transactions and pessimistic row locking.

## Live Demo
https://reservation-system-ten-black.vercel.app/

## Tech Stack

- **Frontend:** React, TypeScript, Vite
- **Backend:** Java 17, Spring Boot, Spring Data JPA
- **Database:** PostgreSQL
- **Testing:** JUnit, Mockito, MockMvc
- **DevOps:** Docker, Docker Compose, GitHub Actions
- **Deployment:** Railway, Vercel

## Key Features

- Create and view products
- Create reservations
- Confirm and cancel reservations
- Automatic stock deduction and restoration
- Prevent overselling with `PESSIMISTIC_WRITE`
- Validation and structured API errors
- DTO-based API responses
- Automated backend tests and CI

## Reservation Flow

```text
AVAILABLE STOCK
      |
      v
     PENDING
   /        \
  v          v
CONFIRMED  CANCELLED
             |
             v
        STOCK RESTORED
```

Stock is deducted when a reservation is created as `PENDING`.

Confirming does not deduct stock again. Cancelling restores the reserved quantity.

## API Endpoints

### Products

```text
GET  /api/products
POST /api/products
```

### Reservations

```text
GET   /api/reservations
GET   /api/reservations/{id}
POST  /api/reservations
PATCH /api/reservations/{id}/confirm
PATCH /api/reservations/{id}/cancel
```

## Run Locally

### Backend + PostgreSQL

From the project root:

```bash
docker compose up --build
```

Backend runs at:

```text
http://localhost:8080
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at:

```text
http://localhost:5173
```

## Run Tests

```bash
cd backend
mvn test
```

The backend currently includes **18 automated unit and web-layer tests**.

## CI

GitHub Actions automatically:

- runs backend tests
- builds the React frontend
- verifies the backend Docker image can be built
