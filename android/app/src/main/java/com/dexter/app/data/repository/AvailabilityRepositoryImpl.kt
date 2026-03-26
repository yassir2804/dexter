package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.AvailabilityMapper
import com.dexter.app.domain.model.Availability
import com.dexter.app.domain.repository.AvailabilityRepository
import java.io.IOException

class AvailabilityRepositoryImpl(
    private val availabilityMapper: AvailabilityMapper
) : AvailabilityRepository {

    companion object { private const val TAG = "AvailabilityRepository" }

    override suspend fun getAvailabilityForUser(userId: Long): List<Availability> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get availability for user: $userId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getAvailabilityForUser", e)
        emptyList()
    }

    override suspend fun getAvailabilityById(id: Long): Availability? = try {
        null
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get availability by id: $id", e)
        null
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getAvailabilityById", e)
        null
    }

    override suspend fun saveAvailability(availability: Availability): Availability = try {
        availability
    } catch (e: IOException) {
        Log.e(TAG, "Failed to save availability", e)
        availability
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in saveAvailability", e)
        availability
    }

    override suspend fun deleteAvailability(id: Long) {
        try {
            // no-op placeholder
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error in deleteAvailability", e)
        }
    }
}
