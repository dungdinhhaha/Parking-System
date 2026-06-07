# Backend Scope

## In Scope

- Authentication and session login
- Role-based authorization
- User management
- Vehicle management
- Infrastructure management
  - Building
  - Floor
  - Zone
  - Slot
- Reservation
- Allocation strategy
- Check-in
- Check-out
- Pricing and fee calculation
- Payment
- Incident handling
- Reports
- Mock AI plate recognition

## Remaining Backend Work
 
- None

## Out of Scope

- Feedback

## Notes for Frontend / AI Handoff

- Treat the existing API response envelope as the contract: `success`, `message`, `data`, `timestamp`, `status`.
- Feedback should not be implemented or consumed by the frontend at this stage.
- Frontend can work independently against the current endpoints for auth, user management, vehicle management, infrastructure, reservation, session flow, payment, incident, and report.
- AI integration should only depend on the plate-recognition provider contract and the mock endpoint that already exists.
