# Dexter Backend

API REST del proyecto Dexter (SkillSwap). Spring Boot 4 + Kotlin 2.2 + PostgreSQL + JWT.

## Arranque

    docker compose up -d db
    ./gradlew bootRun

Arranca en http://localhost:3000 con perfil dev. Postgres en localhost:5432 (db dexter, user/pass dexter).

## Perfiles

- dev — Postgres local, ddl-auto=update (default)
- initlocal — Postgres local, ddl-auto=create (recrear schema + sembrar datos)
- heroku — lee JDBC URL de variable de entorno

## Seed de datos

    ./gradlew test --tests com.dexter.backend.LoadInitData -Dspring.profiles.active=initlocal

Crea roles, privilegios, 2 usuarios (admin + user), skills, matches, 1 sesión completada y 1 review.

## Endpoints activos (fases 0-6)

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| GET | /v1/health | No | Smoke test |
| POST | /v1/auth/register | No | Registra un usuario |
| POST | /v1/users/login | No | Login → JWT en header Authorization |
| GET | /v1/profile-summary?userId= | Bearer | Perfil completo (datos + skills + reviews) |
| PUT | /v1/profile-summary?userId= | Bearer | Actualiza bio/country/avatarUrl |
| GET | /v1/matches?userId=&status= | Bearer | Lista de matches |
| GET | /v1/matches/{id} | Bearer | Match por id |
| GET | /v1/match-summaries?userId= | Bearer | Proyección compacta para Home |
| GET | /v1/search/categories | Bearer | Categorías únicas de skills |
| GET | /v1/search/skills?query=&category= | Bearer | Skills populares / buscadas / filtradas |
| GET | /v1/search/people?skill= | Bearer | Personas que ofrecen una skill |

## Endpoints planeados (Fase 8+, esqueletos en controller/)

| Recurso | Ruta base | Métodos |
|---|---|---|
| Session | /v1/sessions | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| SessionInvitation | /v1/session-invitations | GET, POST, PATCH/{id}/accept, PATCH/{id}/reject |
| Conversation | /v1/conversations | GET, GET/{id} |
| Message | /v1/conversations/{id}/messages | GET, POST |
| Review | /v1/reviews | GET, POST |
| ReviewTag | /v1/review-tags | GET |
| Notification | /v1/notifications | GET, PATCH/{id}/read |
| Availability | /v1/availability | GET, POST, PUT/{id}, DELETE/{id} |

Cada método devuelve HTTP 500 con NotImplementedError("Fase 8+: ...") hasta que se implemente.

## Estructura de paquetes

    com.dexter.backend/
    ├── entity/              # @Entity JPA
    ├── repository/          # JpaRepository interfaces
    ├── dto/                 # DTOs de request/response
    ├── mapper/              # MapStruct
    ├── service/             # interface XService + AbstractXService impl
    ├── controller/          # @RestController
    ├── security/            # JWT filters, LoggedUser, AppCustomDsl
    ├── config/              # JwtSecurityConfiguration, OpenSecurityConfiguration
    └── error/               # ApiError, RestExceptionHandler

## Contrato con el frontend

Los DTOs del backend coinciden campo a campo con los del front en:
frontend-android-delta/app/src/main/java/com/dexter/app/data/datasource/model/*.kt

BASE_URL del front: http://10.0.2.2:3000/v1/ (emulador → host).
Desde Fase 6 el front tiene AuthInterceptor (inyecta Authorization: Bearer JWT) + TokenStore (DataStore).
