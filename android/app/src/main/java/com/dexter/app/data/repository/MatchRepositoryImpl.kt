package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.MatchMapper
import com.dexter.app.domain.model.Match
import com.dexter.app.domain.repository.MatchRepository
import java.io.IOException

class MatchRepositoryImpl(
    private val matchMapper: MatchMapper
) : MatchRepository {

    companion object { private const val TAG = "MatchRepository" }

    override suspend fun getMatchById(id: Long): Match? = try {
        null
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get match by id: $id", e)
        null
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getMatchById", e)
        null
    }

    override suspend fun getMatchesForUser(userId: Long): List<Match> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get matches for user: $userId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getMatchesForUser", e)
        emptyList()
    }

    override suspend fun createMatch(match: Match): Match = try {
        match
    } catch (e: IOException) {
        Log.e(TAG, "Failed to create match", e)
        match
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in createMatch", e)
        match
    }

    override suspend fun updateMatch(match: Match): Match = try {
        match
    } catch (e: IOException) {
        Log.e(TAG, "Failed to update match", e)
        match
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in updateMatch", e)
        match
    }
}
