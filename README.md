# FamilyNet - Premium Family/Clan Social App

FamilyNet is a connected private social network for family and clan workflows.

## Tech Stack
- Android: Kotlin, Jetpack Compose, MVVM, Navigation, Repository, Retrofit, OkHttp WebSocket, Coil
- Backend: Kotlin, Spring Boot, Spring Security, JWT, JPA, WebSocket, Validation
- Database: MySQL 8

## Major Capabilities
- Family Tree: parent/child/sibling/spouse graph with bidirectional sync
- Member Profile: role, avatar, personal profile, relationship context
- Timeline: real posts + comments + image feed
- Family Chat: realtime chat over raw WebSocket + HTTP fallback
- Events: create, RSVP (GOING/MAYBE/DECLINED), dashboard reminders
- Roles: server-side admin/member enforcement with token-based identity

## Architecture Upgrade
- Authentication is now JWT-based (`/auth/login`) instead of client-side actorId simulation
- API write operations derive actor/member from token (stateless auth)
- Android has `core/session` and `core/realtime` layers (session token + socket client)
- Features are connected via one shared `FamilyViewModel` state flow

## Project Layout
- `backend/`: Spring Boot API + security + MySQL persistence
- `android-app/`: Compose app with MVVM + realtime

## Backend Run
1. Start database:
   - `docker compose up -d`
2. Run backend:
   - `cd backend`
   - `gradlew.bat bootRun`
3. Service URL:
   - `http://localhost:8080`

## Seed Accounts
- `admin` / `admin123` (ADMIN)
- `linh` / `linh123` (MEMBER)

## Android Run
1. Open `android-app/` in Android Studio
2. Sync Gradle
3. Run app on emulator/device
4. Base URL uses emulator loopback: `http://10.0.2.2:8080/`

## Key Endpoints
- `POST /auth/login`
- `GET /auth/me`
- `GET /api/dashboard`
- `GET /api/members`
- `POST /api/members/{id}/relationships`
- `GET /api/tree/{memberId}`
- `GET /api/timeline`
- `POST /api/timeline/posts`
- `POST /api/timeline/posts/{postId}/comments`
- `GET /api/events`
- `POST /api/events`
- `POST /api/events/{eventId}/rsvp`
- `GET /api/chat/messages?limit=80`
- `POST /api/chat/messages`
- Realtime socket: `ws://localhost:8080/ws-chat-raw?token=JWT`

## Security Notes
- Admin-only operations are enforced server-side using token role:
  - create member
  - create relationship
  - create event
- Password hashes are not serialized in API responses.
