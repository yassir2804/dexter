package com.dexter.app.presentation.navigation

/**
 * Type-safe route definitions for the app navigation graph.
 *
 * Each destination is a data object with a compile-time ROUTE constant,
 * following the same sealed-class pattern used in TaskApp's NavRoutes.
 */
sealed class NavRoutes {

    // ── Auth flow (no bottom bar) ───────────────────────────────────

    data object Login : NavRoutes() {
        const val ROUTE = "login"
    }

    data object Welcome : NavRoutes() {
        const val ROUTE = "welcome"
    }

    data object ForgotPassword : NavRoutes() {
        const val ROUTE = "forgotPassword"
    }

    // ── Main flow (with bottom bar) ─────────────────────────────────

    data object Home : NavRoutes() {
        const val ROUTE = "home"
    }

    data object Search : NavRoutes() {
        const val ROUTE = "search"
    }

    data object Messages : NavRoutes() {
        const val ROUTE = "messages"
    }

    data object Profile : NavRoutes() {
        const val ROUTE = "profile"
    }

    // ── Detail screens (no bottom bar, accessed from main) ──────────

    data object MatchDetail : NavRoutes() {
        const val ROUTE = "matchDetail/{matchId}"
        const val ARG_MATCH_ID = "matchId"
        fun createRoute(matchId: Long) = "matchDetail/$matchId"
    }

    data object Plan : NavRoutes() {
        const val ROUTE = "plan/{matchId}"
        const val ARG_MATCH_ID = "matchId"
        fun createRoute(matchId: Long) = "plan/$matchId"
    }

    data object Chat : NavRoutes() {
        const val ROUTE = "chat/{conversationId}"
        const val ARG_CONVERSATION_ID = "conversationId"
        fun createRoute(conversationId: Long) = "chat/$conversationId"
    }

    data object UserProfile : NavRoutes() {
        const val ROUTE = "userProfile/{userId}"
        const val ARG_USER_ID = "userId"
        fun createRoute(userId: Long) = "userProfile/$userId"
    }

    data object EditProfile : NavRoutes() {
        const val ROUTE = "editProfile"
    }

    data object Settings : NavRoutes() {
        const val ROUTE = "settings"
    }

    data object Notifications : NavRoutes() {
        const val ROUTE = "notifications"
    }

    data object Rate : NavRoutes() {
        const val ROUTE = "rate/{sessionId}"
        const val ARG_SESSION_ID = "sessionId"
        fun createRoute(sessionId: Long) = "rate/$sessionId"
    }

    data object EditSession : NavRoutes() {
        const val ROUTE = "editSession/{sessionId}"
        const val ARG_SESSION_ID = "sessionId"
        fun createRoute(sessionId: Long) = "editSession/$sessionId"
    }

    data object ActiveSession : NavRoutes() {
        const val ROUTE = "activeSession/{sessionId}"
        const val ARG_SESSION_ID = "sessionId"
        fun createRoute(sessionId: Long) = "activeSession/$sessionId"
    }

    data object SessionInvite : NavRoutes() {
        const val ROUTE = "sessionInvite/{invitationId}"
        const val ARG_INVITATION_ID = "invitationId"
        fun createRoute(invitationId: Long) = "sessionInvite/$invitationId"
    }
}
