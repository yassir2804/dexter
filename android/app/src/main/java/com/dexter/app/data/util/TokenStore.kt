package com.dexter.app.data.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "dexter_auth")
private val TOKEN_KEY = stringPreferencesKey("jwt_token")

/**
 * Persiste el JWT del usuario autenticado usando DataStore Preferences.
 *
 * - [getToken]: lectura síncrona (runBlocking) porque los interceptores OkHttp son síncronos.
 * - [saveToken] / [clearToken]: corutinas, llamadas desde ViewModels.
 */
@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getToken(): String? = runBlocking {
        context.dataStore.data.first()[TOKEN_KEY]
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}
