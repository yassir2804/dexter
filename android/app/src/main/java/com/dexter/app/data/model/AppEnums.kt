package com.dexter.app.data.model

enum class SkillRole { OFFERED, SOUGHT }
enum class SkillLevel { BEGINNER, INTERMEDIATE, ADVANCED }
enum class MatchStatus { PENDING, ACCEPTED, REJECTED, CANCELLED }
enum class SessionStatus { SCHEDULED, COMPLETED, CANCELLED }
enum class InvitationStatus { PENDING, ACCEPTED, REJECTED, EXPIRED }
enum class MessageStatus { SENT, DELIVERED, READ }
enum class NotificationType { NEW_MATCH, SESSION_REMINDER, NEW_MESSAGE, NEW_REVIEW, AI_SUGGESTION }
