
# RESQNET — Intelligent Disaster Response & Resource Allocation System

A software-only disaster management and decision-support platform: incident reporting and
verification, an intelligent priority engine, weighted resource allocation, Dijkstra-based
route optimization with dynamic recalculation, shelter management, and a real-time
control-room dashboard with a Leaflet + OpenStreetMap live map.

## 1. Problem Statement
During disasters, coordinators must decide — under time pressure and incomplete information —
which incidents to respond to first, which resources to send, and which roads are safe. Manual,
FIFO-based response is slow and error-prone. RESQNET automates prioritization, allocation, and
routing with transparent, explainable algorithms.

## 2. Technology Stack
**Backend:** Java 21, Spring Boot 3.3, Spring Web, Spring Data JPA, Spring Security (JWT, BCrypt),
Spring WebSocket (STOMP), Maven, MySQL 8.
**Frontend:** React 18, React Router, Axios, Leaflet + React-Leaflet + OpenStreetMap, Recharts,
@stomp/stompjs + SockJS.

## 3. User Roles
- **ADMIN** — manage users, resources, teams, shelters, roads; full visibility.
- **COORDINATOR** — verify incidents, assign resources/teams, manage shelters/roads.
- **RESCUE_TEAM** — view assigned incidents/routes, update operation status.
- **CITIZEN** — report disasters, track status, view nearby shelters.

## 4. Core Algorithms
- **Priority Engine** (`PriorityEngine.java`) — weighted rule-based score (0-100): severity 30%,
  affected people 20%, medical urgency 20%, vulnerability 10%, response time 10%, disaster type 10%.
  O(1) per incident.
- **Incident Priority Queue** (`IncidentService.getActiveIncidentsByPriority`) — active incidents
  are sorted by priority score in the database, with a stable ID tie-breaker.
- **Allocation Engine** (`AllocationEngine.java`) — weighted greedy scoring (priority, suitability,
  distance, availability, response time) ranks every candidate resource; never "first available".
  O(n log n) per incident.
- **Route Engine** (`RouteEngine.java`) — Dijkstra's algorithm over a graph built from the `roads`
  table (blocked roads are excluded from the graph entirely). Cost = distance + traffic penalty +
  flood risk + road-condition penalty + blockage penalty. O((V+E) log V). Structured so an A*
  heuristic (Haversine-based) can be added later without changing the public API.
- **Dynamic Route Recalculation** — `RouteService.recalculateRoute` re-runs Dijkstra after a road
  condition update and pushes the new route over WebSocket (`/topic/route-updates`).
- **Haversine Distance** (`HaversineUtil.java`) — great-circle distance for all lat/lng scoring.

## 5. Project Structure
```
resqnet/
├── backend/     Spring Boot (Java 21, Maven)
│   └── src/main/java/com/resqnet/
│       ├── config/       JWT, Spring Security, CORS, WebSocket
│       ├── controller/   REST controllers
│       ├── dto/          Request/response DTOs
│       ├── entity/       JPA entities
│       ├── enums/        Domain enums
│       ├── exception/    Global exception handling
│       ├── repository/   Spring Data JPA repositories
│       ├── service/      Business logic + algorithms
│       └── util/         HaversineUtil
│   └── src/main/resources/
│       ├── application.properties
│       └── db/migration/ Versioned Flyway schema and demo-data migrations
└── frontend/    React (Create React App)
    └── src/
        ├── api/          Axios instance with JWT interceptor
        ├── components/   Sidebar, Navbar, Layout, DisasterMap (Leaflet)
        ├── context/       AuthContext
        ├── pages/         Login, Register, Dashboard, Incidents, Map, Resources, ...
        └── styles/
```

## 6. Setup — Backend

### Prerequisites
- Java 21 (JDK)
- Maven 3.9+
- MySQL 8+ running locally

### Steps
```bash
cd backend

# 1. Create the database (or let the MySQL connection create it)
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS resqnet_db;"

# 2. Configure the MySQL connection in src/main/resources/application.properties

# 3. Build and run
mvn clean install
mvn spring-boot:run
```
Backend runs at `http://localhost:8080`. Seed data loads automatically on first run
through Flyway migrations. New databases get the complete schema and demo data.
For databases created by an earlier RESQNET version, Flyway baselines the existing
schema at version 1 and applies the demo-data migration without recreating tables.
Hibernate validates the schema at startup rather than modifying it automatically.
The demo migrations also create sample incidents, incident timelines, and a critical-incident
notification so the dashboard and incident views have data on first startup. Existing databases
apply new demo records through the next Flyway migration without deleting existing data.

**Demo login:** `admin@resqnet.in` / `Password@123` (all seeded users share this password —
change it after first login in a real deployment).

## 7. Setup — Frontend

### Prerequisites
- Node.js 18+ and npm

### Steps
```bash
cd frontend
npm install
npm start
```
Frontend runs at `http://localhost:3000` and talks to the backend at `http://localhost:8080/api`
(configurable via `REACT_APP_API_URL` in a `.env` file).

## 8. REST API Overview
```
POST   /api/auth/register
POST   /api/auth/login

GET    /api/incidents
GET    /api/incidents/priority-queue
POST   /api/incidents
PUT    /api/incidents/{id}/verify
PUT    /api/incidents/{id}/status?status=RESOLVED
GET    /api/incidents/{id}/timeline

GET    /api/resources            POST /api/resources           PUT/DELETE /api/resources/{id}
GET    /api/rescue-teams         POST /api/rescue-teams        PUT/DELETE /api/rescue-teams/{id}
GET    /api/shelters             POST /api/shelters            GET /api/shelters/recommend
GET    /api/roads                POST /api/roads               PUT /api/roads/{id}/status

POST   /api/allocations/incident/{id}/auto-allocate
GET    /api/allocations

POST   /api/routes/calculate
POST   /api/routes/recalculate

GET    /api/dashboard/statistics
GET    /api/notifications

POST   /api/simulate/flood?lat=..&lng=..&locationName=..
POST   /api/simulate/road-blockage/{roadId}
```
WebSocket (STOMP over SockJS) endpoint: `ws://localhost:8080/ws`, topics
`/topic/notifications`, `/topic/road-updates`, `/topic/route-updates`.

## 9. Demo Scenario
1. Log in as coordinator (`ravi.coordinator@resqnet.in`).
2. Go to **Incidents → Report Incident**, or call `POST /api/simulate/flood` to auto-generate
   a critical flood at Karur with resources auto-allocated.
3. Open **Map** to see the incident, resources, teams, and shelters plotted with Leaflet/OSM.
4. Use the route finder to compute the Dijkstra route between two road nodes.
5. Go to **Roads**, mark a road on that route `BLOCKED`, then recompute the route — it
   automatically avoids the blocked segment.
6. Check **Incidents → [incident] → Timeline** for the full audit trail.
7. Check **Analytics** for charts by type/priority/status.

## 10. Notes on Scope
This delivery implements the full backend architecture and algorithm layer (priority engine,
allocation engine, Dijkstra routing, dynamic recalculation) plus a working React + Leaflet
frontend covering every module's CRUD and the live map/dashboard/analytics. Left as extension
points for you to build on directly on top of this codebase:
- Fine-grained role-based UI gating per screen (backend already enforces role-based authorization).
- JUnit/Mockito test suite (service layer is written against interfaces/DI, so it's test-ready).
- Docker Compose for one-command startup.
- Image upload storage (currently accepts an `imageUrl` string field).

## 11. Future Enhancements
- A* pathfinding with Haversine heuristic (drop-in alongside `RouteEngine`).
- ML-based severity prediction service, integrated via a new Spring service bean.
- Push notifications / SMS integration for citizens near an incident.
- Mobile app (React Native) reusing the same REST/WebSocket API.
=======
# ResQnet
>>>>>>> f278de4d50c1be2e9ed324d06226f0c35444fbeb
