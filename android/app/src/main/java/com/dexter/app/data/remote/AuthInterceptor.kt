package com.dexter.app.data.remote

import com.dexter.app.data.util.TokenStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor OkHttp que inyecta el header `Authorization: Bearer <JWT>` en cada request
 * cuando hay un token guardado en [TokenStore].
 *
 * Así los ViewModels no necesitan pasar el token manualmente a cada ApiService.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStore.getToken()
        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
