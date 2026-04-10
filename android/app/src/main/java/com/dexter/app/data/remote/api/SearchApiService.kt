package com.dexter.app.data.remote.api

import com.dexter.app.data.datasource.model.PersonResultDto
import com.dexter.app.data.datasource.model.SkillSummaryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API Service for search operations.
 *
 * Endpoints:
 * - GET /search/categories          → list of category strings
 * - GET /search/skills              → popular skills (optional query/category filters)
 * - GET /search/people?skill=...    → people offering a specific skill
 */
interface SearchApiService {

    @GET("search/categories")
    suspend fun getCategories(): Response<List<String>>

    @GET("search/skills")
    suspend fun getPopularSkills(): Response<List<SkillSummaryDto>>

    @GET("search/skills")
    suspend fun searchSkills(
        @Query("query") query: String
    ): Response<List<SkillSummaryDto>>

    @GET("search/skills")
    suspend fun filterSkillsByCategory(
        @Query("category") category: String
    ): Response<List<SkillSummaryDto>>

    @GET("search/people")
    suspend fun getPeopleForSkill(
        @Query("skill") skillName: String
    ): Response<List<PersonResultDto>>
}
