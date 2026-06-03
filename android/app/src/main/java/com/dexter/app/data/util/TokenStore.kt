package com.dexter.app.data.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "dexter_auth")
private val TOKEN_KEY = stringPreferencesKey("jwt_token")
private val USER_ID_KEY = longPreferencesKey("user_id")
private val USER_EMAIL_KEY = stringPreferencesKey("user_email")

/**
 * Persiste la sesion del usuario autenticado usando DataStore Preferences.
 *
 * Guardamos no solo el JWT sino tambien el `userId` y `email` devueltos
 * en el body del login. Esto permite que las llamadas posteriores que
 * requieren `userId` como query param (PUT /profile-summary?userId=...,
 * GET /matches?userId=..., etc.) lo puedan leer sin tener que parsearlo
 * del JWT cada vez.
 */
@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getToken(): String? = runBlocking {
        context.dataStore.data.first()[TOKEN_KEY]
    }

    fun getUserId(): Long? = runBlocking {
        context.dataStore.data.first()[USER_ID_KEY]
    }

    fun getUserEmail(): String? = runBlocking {
        context.dataStore.data.first()[USER_EMAIL_KEY]
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun saveSession(token: String, userId: Long?, email: String?) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            if (userId != null) prefs[USER_ID_KEY] = userId
            if (!email.isNullOrBlank()) prefs[USER_EMAIL_KEY] = email
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit {
            it.remove(TOKEN_KEY)
            it.remove(USER_ID_KEY)
            it.remove(USER_EMAIL_KEY)
        }
    }
}
