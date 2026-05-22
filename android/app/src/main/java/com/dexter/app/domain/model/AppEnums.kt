package com.dexter.app.domain.model

/**
 * Domain-level enums used across the model.
 *
 * These enums represent business rules (allowed values) and live in the domain
 * package to keep the domain layer independent from persistence/serialization.
 */
enum class SkillRole { OFFERED, SOUGHT }

enum class SkillLevel { BEGINNER, INTERMEDIATE, ADVANCED }

enum class MatchStatus { PENDING, ACCEPTED, REJECTED, CANCELLED }

enum class SessionStatus { SCHEDULED, COMPLETED, CANCELLED }

enum class InvitationStatus { PENDING, ACCEPTED, REJECTED, EXPIRED }

enum class NotificationType { NEW_MATCH, SESSION_REMINDER, NEW_REVIEW, AI_SUGGESTION }
