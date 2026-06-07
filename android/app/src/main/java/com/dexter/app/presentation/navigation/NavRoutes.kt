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

    data object Onboarding : NavRoutes() {
        const val ROUTE = "onboarding/{email}/{password}"
        const val ARG_EMAIL = "email"
        const val ARG_PASSWORD = "password"
        fun createRoute(email: String, password: String) =
            "onboarding/${android.net.Uri.encode(email)}/${android.net.Uri.encode(password)}"
    }

    // ── Main flow (with bottom bar) ─────────────────────────────────

    data object Home : NavRoutes() {
        const val ROUTE = "home"
    }

    data object Search : NavRoutes() {
        const val ROUTE = "search"
    }

    data object Matches : NavRoutes() {
        const val ROUTE = "matches"
    }

    data object Profile : NavRoutes() {
        const val ROUTE = "profile"
    }

    data object AdminDashboard : NavRoutes() {
        const val ROUTE = "adminDashboard"
    }

    data object AdminSettings : NavRoutes() {
        const val ROUTE = "adminSettings"
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

    data object UserProfile : NavRoutes() {
        const val ROUTE = "userProfile/{userId}?searchedSkill={searchedSkill}"
        const val ARG_USER_ID = "userId"
        const val ARG_SEARCHED_SKILL = "searchedSkill"
        fun createRoute(userId: Long, searchedSkill: String? = null): String =
            if (searchedSkill != null)
                "userProfile/$userId?searchedSkill=${android.net.Uri.encode(searchedSkill)}"
            else
                "userProfile/$userId"
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

    data object PeopleList : NavRoutes() {
        const val ROUTE = "peopleList/{skillName}"
        const val ARG_SKILL_NAME = "skillName"
        fun createRoute(skillName: String) = "peopleList/${android.net.Uri.encode(skillName)}"
    }
}
