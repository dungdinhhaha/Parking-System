# Parking System

Spring Boot scaffold for the parking management system described in the PDF.

## Structure

- `controller`: API entry points by domain
- `facade`: orchestration of check-in/check-out flows
- `service`: business operations and implementation layer
- `strategy`: allocation logic for car and motorbike parking
- `policy`: fee policy abstraction
- `processor`: payment processor abstraction
- `repository`: persistence contracts
- `entity`: domain models
- `dto`: request and response models
- `enums`: shared status and type enums
- `adapter`: external integrations, including AI OCR
- `storage`: file storage abstraction
- `config`: Spring configuration

This project is organized as a modular Spring Boot backend for the parking management system.

## Current backend scope

Implemented and ready for handoff:

- Authentication and session-based authorization
- Infrastructure management: buildings, floors, zones, slots
- Reservation management
- Allocation strategy for check-in
- Check-in and check-out
- Pricing and fee calculation
- Payment processing
- Incident management
- Report dashboards
- Mock AI plate recognition

User management and vehicle management are now implemented as separate backend modules.

Out of scope for the current handoff:

- Feedback

## Supabase connection

Set these environment variables before running the app:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## IntelliJ Run

- Open the `Run With Supabase` configuration, or
- copy `.env.example` to `.env` and fill `SPRING_DATASOURCE_PASSWORD`

## Authentication

- `POST /api/auth/login` creates a server session after username/password login.
- `GET /api/auth/me` returns the current authenticated user.
- `POST /api/auth/logout` invalidates the session.
- Open `/auth-test.html` to test login, logout, and role-protected endpoints from the browser.
- Most API responses use a common envelope: `success`, `message`, `data`, `timestamp`, `status`.

## Seed auth users

Default seeded accounts on startup:

- `manager` / `manager123`
- `staff` / `staff123`
- `driver` / `driver123`
- `admin` / `admin123`

These users are created only if the username does not already exist.

On startup, the app also migrates legacy `users_role_check` values from the old role names to `MANAGER`, `STAFF`, `DRIVER`, and `SYSTEM_ADMIN`.

## Infrastructure management

- `POST /api/buildings`, `GET /api/buildings`, `GET /api/buildings/{id}`, `PUT /api/buildings/{id}`, `DELETE /api/buildings/{id}`
- `POST /api/floors`, `GET /api/floors`, `GET /api/floors/{id}`, `PUT /api/floors/{id}`, `DELETE /api/floors/{id}`
- `POST /api/zones`, `GET /api/zones`, `GET /api/zones/{id}`, `PUT /api/zones/{id}`, `DELETE /api/zones/{id}`
- `POST /api/slots`, `GET /api/slots`, `GET /api/slots/{id}`, `PUT /api/slots/{id}`, `DELETE /api/slots/{id}`

These endpoints are restricted to `MANAGER` and `SYSTEM_ADMIN`.

## Infrastructure test page

- Open `/infra-test.html` after logging in with `manager` or `admin`.
- The page can create and list buildings, floors, zones, and slots.

`buildingCode` is now the unique business key for buildings. The sample seed uses `BLD-001`.

## Reservation

- `POST /api/reservations` creates a reservation for the logged-in user.
- `GET /api/reservations/me` lists the current user's reservations.
- `GET /api/reservations/{id}` returns a reservation owned by the current user.
- `DELETE /api/reservations/{id}` cancels a reservation owned by the current user.
- `GET /api/reservations` lists all reservations for `MANAGER` and `SYSTEM_ADMIN`.

Reservation rules:
- `CAR` requires both `zoneId` and `slotId`.
- `MOTORBIKE` requires `zoneId`.
- Car reservations reserve a concrete `ParkingSlot`.
- Motorbike reservations increase `reservedCount` on `ParkingZone`.
- No payment is created at reservation time.

## Reservation test page

- Open `/reservation-test.html` after logging in with `driver`, `manager`, or `admin`.
- The page can create, cancel, and list reservations.
- It also loads availability from `GET /api/reservations/availability?buildingCode=BLD-001` so a driver can pick only slots or zones that are actually free.
- Manager-only actions are hidden when the session user is not `MANAGER` or `SYSTEM_ADMIN`.

Reservation date validation uses `@FutureOrPresent`, so the driver can book the current time or a later time.

## Manual test pages

Open `/test-hub.html` for quick access to all browser-based feature tests.

- `/auth-test.html`
- `/infra-test.html`
- `/reservation-test.html`
- `/session-flow-test.html`
- `/user-test.html`
- `/vehicle-test.html`
- `/payment-test.html`
- `/incident-test.html`
- `/report-test.html`
- `/ai-test.html`

## Hand-off notes

If you are building the frontend or AI layer separately, use the current backend contract as the source of truth. The backend scope is now complete for the current delivery, and feedback is not part of the active scope.

## User management

- `POST /api/users`
- `GET /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `PATCH /api/users/{id}/status`
- `PATCH /api/users/{id}/password`

These endpoints are restricted to `MANAGER` and `SYSTEM_ADMIN`.

## Vehicle management

- `POST /api/vehicles`
- `GET /api/vehicles/me`
- `GET /api/vehicles`
- `GET /api/vehicles/{id}`
- `PUT /api/vehicles/{id}`
- `DELETE /api/vehicles/{id}`

Drivers can manage their own vehicles. `GET /api/vehicles` is restricted to `MANAGER` and `SYSTEM_ADMIN`.
