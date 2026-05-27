package com.dexter.app.data.di

import com.dexter.app.data.remote.AuthInterceptor
import com.dexter.app.data.remote.api.MatchApiService
import com.dexter.app.data.remote.api.NotificationApiService
import com.dexter.app.data.remote.api.ReviewApiService
import com.dexter.app.data.remote.api.ReviewTagApiService
import com.dexter.app.data.remote.api.SearchApiService
import com.dexter.app.data.remote.api.SessionApiService
import com.dexter.app.data.remote.api.SessionInvitationApiService
import com.dexter.app.data.remote.api.UserApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for network dependencies.
 *
 * Step 1 scope: only base network objects (no API service yet).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Heroku cloud backend
    private const val BASE_URL = "https://dexter-backend-84cdf8564cb3.herokuapp.com/v1/"

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        // Registrado antes del logging para que el header Authorization aparezca en los logs.
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .writeTimeout(8, TimeUnit.SECONDS)
        .callTimeout(10, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideMatchApiService(retrofit: Retrofit): MatchApiService =
        retrofit.create(MatchApiService::class.java)

    @Provides
    @Singleton
    fun provideSearchApiService(retrofit: Retrofit): SearchApiService =
        retrofit.create(SearchApiService::class.java)

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService =
        retrofit.create(NotificationApiService::class.java)

    @Provides
    @Singleton
    fun provideReviewApiService(retrofit: Retrofit): ReviewApiService =
        retrofit.create(ReviewApiService::class.java)

    @Provides
    @Singleton
    fun provideReviewTagApiService(retrofit: Retrofit): ReviewTagApiService =
        retrofit.create(ReviewTagApiService::class.java)

    @Provides
    @Singleton
    fun provideSessionApiService(retrofit: Retrofit): SessionApiService =
        retrofit.create(SessionApiService::class.java)

    @Provides
    @Singleton
    fun provideSessionInvitationApiService(retrofit: Retrofit): SessionInvitationApiService =
        retrofit.create(SessionInvitationApiService::class.java)
}
