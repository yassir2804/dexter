package com.dexter.app.data.datasource

import com.dexter.app.domain.model.ProfileReviewItem
import com.dexter.app.domain.model.ProfileSummary
import com.dexter.app.domain.model.User
import java.util.Date

/**
 * Simple in-memory provider that simulates a backend for `User` data.
 */
class UserProvider {
    companion object {
        private val userList = listOf(
            User(
                id = 1,
                email = "alice@example.com",
                password = "password123",
                firstName = "Alice",
                lastName = "Gonzalez",
                username = "alice",
                bio = "Guitar teacher",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&h=150&fit=crop&crop=face",
                country = "CR",
                enabled = true,
                tokenExpired = false,
                rating = 4.8,
                totalSessions = 12,
                totalMatches = 5,
                createDate = Date(),
                updatedAt = Date()
            ),
            User(
                id = 2,
                email = "bob@example.com",
                password = "password123",
                firstName = "Bob",
                lastName = "Smith",
                username = "bob",
                bio = "Learning Spanish",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face",
                country = "US",
                enabled = true,
                tokenExpired = false,
                rating = 4.2,
                totalSessions = 4,
                totalMatches = 3,
                createDate = Date(),
                updatedAt = Date()
            )
        )

        fun findUserById(id: Long): User? = userList.find { it.id == id }

        fun findAllUsers(): List<User> = userList

        fun findUserByEmail(email: String): User? = userList.find { it.email == email }

        // Mock profile data matching the React prototype (ProfileScreen.tsx)
        private val sampleProfiles = mapOf(
            1L to ProfileSummary(
                initials = "AG",
                firstName = "Alice",
                lastName = "Gonzalez",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&h=150&fit=crop&crop=face",
                bio = "Guitar teacher",
                country = "Costa Rica",
                phone = "+506 8888-1234",
                rating = 4.8,
                totalSessions = 12,
                totalMatches = 5,
                skillsOffered = listOf("Guitarra", "Inglés", "Marketing"),
                skillsSought = listOf("Fotografía", "Piano"),
                reviews = listOf(
                    ProfileReviewItem(
                        reviewerName = "Bob Smith",
                        reviewerAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face",
                        rating = 5,
                        comment = "Excelente profesora de guitarra."
                    ),
                    ProfileReviewItem(
                        reviewerName = "Bob Smith",
                        reviewerAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face",
                        rating = 4,
                        comment = "Buenas sesiones de inglés."
                    )
                )
            ),
            2L to ProfileSummary(
                initials = "BS",
                firstName = "Bob",
                lastName = "Smith",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face",
                bio = "Learning Spanish",
                country = "United States",
                phone = null,
                rating = 4.2,
                totalSessions = 4,
                totalMatches = 3,
                skillsOffered = listOf("Programación", "React"),
                skillsSought = listOf("Español", "Diseño gráfico"),
                reviews = emptyList()
            )
        )

        fun findProfileSummary(userId: Long): ProfileSummary? = sampleProfiles[userId]
    }
}
