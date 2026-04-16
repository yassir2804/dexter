package com.dexter.app.data.di

import com.dexter.app.data.repository.MatchRepositoryImpl
import com.dexter.app.data.repository.NotificationRepositoryImpl
import com.dexter.app.data.repository.ReviewRepositoryImpl
import com.dexter.app.data.repository.ReviewTagRepositoryImpl
import com.dexter.app.data.repository.SearchRepositoryImpl
import com.dexter.app.data.repository.SessionInvitationRepositoryImpl
import com.dexter.app.data.repository.SessionRepositoryImpl
import com.dexter.app.data.repository.UserRepositoryImpl
import com.dexter.app.domain.repository.MatchRepository
import com.dexter.app.domain.repository.NotificationRepository
import com.dexter.app.domain.repository.ReviewRepository
import com.dexter.app.domain.repository.ReviewTagRepository
import com.dexter.app.domain.repository.SearchRepository
import com.dexter.app.domain.repository.SessionInvitationRepository
import com.dexter.app.domain.repository.SessionRepository
import com.dexter.app.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * LAB 3 - RepositoryModule — Patrón basado en TaskApp (@Binds)
 *
 * Este módulo usa @Binds (más eficiente que @Provides) para mapear
 * interfaces a sus implementaciones.
 *
 * ¿Por qué @Binds es mejor que @Provides?
 * - @Provides: Hilt genera un método que crea y retorna la instancia
 * - @Binds: Hilt simplemente castea la implementación a la interfaz (0 overhead)
 * - Resultado: Código más limpio, compilación más rápida
 *
 * Cómo funciona:
 * 1. ViewModel solicita MatchRepository (interfaz)
 *    ↓
 * 2. Hilt ve este módulo y busca: "¿quién proporciona MatchRepository?"
 *    ↓
 * 3. Encuentra: @Binds abstract fun bindMatchRepository(MatchRepositoryImpl)
 *    ↓
 * 4. Hilt inyecta automáticamente MatchRepositoryImpl como MatchRepository
 *    ↓
 * 5. MatchRepositoryImpl ya está configurado con @Inject constructor
 *    (Hilt inyecta DataSource, Mappers, etc.)
 *    ↓
 * 6. Result: app funciona con datos reales de Retrofit ✅
 *
 * NOTA: abstract class (no object) porque @Binds funciona solo en métodos abstract
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Vincula la interfaz MatchRepository a su implementación MatchRepositoryImpl.
     *
     * @param matchRepositoryImpl La implementación (Hilt la inyecta automáticamente)
     * @return La interfaz que el ViewModel pide
     *
     * Hilt automáticamente inyecta en MatchRepositoryImpl:
     * - dataSource: MatchDataSource (que viene de DataSourceImpl)
     * - matchMapper: MatchMapper  (que viene de @Inject constructor)
     * - matchSummaryMapper: MatchSummaryMapper (que viene de @Inject constructor)
     *
     * Cadena completa:
     * MatchRepositoryImpl @Inject constructor(
     *     private val dataSource: MatchDataSource,  ← MatchDataSourceImpl (con RemoteDataSource)
     *     private val matchMapper: MatchMapper,
     *     private val matchSummaryMapper: MatchSummaryMapper
     * )
     */
    @Binds
    @Singleton
    abstract fun bindMatchRepository(
        matchRepositoryImpl: MatchRepositoryImpl
    ): MatchRepository

    /**
     * Vincula UserRepository a UserRepositoryImpl para login y perfil.
     */
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: SessionRepositoryImpl
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(
        reviewRepositoryImpl: ReviewRepositoryImpl
    ): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindReviewTagRepository(
        reviewTagRepositoryImpl: ReviewTagRepositoryImpl
    ): ReviewTagRepository

    @Binds
    @Singleton
    abstract fun bindSessionInvitationRepository(
        sessionInvitationRepositoryImpl: SessionInvitationRepositoryImpl
    ): SessionInvitationRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}
