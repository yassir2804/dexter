package com.dexter.app.data.datasource

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
                avatarUrl = null,
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
                avatarUrl = null,
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
    }
}
