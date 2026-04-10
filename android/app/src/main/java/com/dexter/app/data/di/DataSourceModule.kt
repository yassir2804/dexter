package com.dexter.app.data.di

import com.dexter.app.data.datasource.MatchDataSource
import com.dexter.app.data.datasource.MatchDataSourceImpl
import com.dexter.app.data.datasource.SearchDataSource
import com.dexter.app.data.datasource.SearchDataSourceImpl
import com.dexter.app.data.datasource.UserDataSource
import com.dexter.app.data.datasource.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Bindings for data source interfaces.
 *
 * Hilt needs these bindings so repository constructors can request the
 * interface types instead of concrete implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindUserDataSource(
        userDataSourceImpl: UserDataSourceImpl
    ): UserDataSource

    @Binds
    @Singleton
    abstract fun bindMatchDataSource(
        matchDataSourceImpl: MatchDataSourceImpl
    ): MatchDataSource

    @Binds
    @Singleton
    abstract fun bindSearchDataSource(
        searchDataSourceImpl: SearchDataSourceImpl
    ): SearchDataSource
}