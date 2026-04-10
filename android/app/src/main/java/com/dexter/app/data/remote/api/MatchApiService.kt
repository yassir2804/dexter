package com.dexter.app.data.remote.api

import com.dexter.app.data.datasource.model.MatchDto
import com.dexter.app.data.datasource.model.MatchSummaryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * LAB 3 - Retrofit API Service Interface
 *
 * Esta interfaz define todos los endpoints (URLs) que usaremos para comunicarnos
 * con el backend de Dexter. Las anotaciones @GET, @POST, etc. le dicen a Retrofit
 * cómo construir cada solicitud HTTP.
 *
 * Retrofit genera automáticamente la implementación en tiempo de compilación.
 * No necesitamos escribir el código HTTP manualmente.
 *
 * Ejemplo de cómo Retrofit interpreta @GET("/matches/{userId}"):
 * - @GET indica que es una solicitud GET
 * - "/matches/{userId}" es la ruta relativa (Retrofit la añade a BASE_URL)
 * - @Path("userId") toma el parámetro userId y lo sustituye en la URL
 * - Resultado: GET http://10.0.2.2:3000/matches/123
 */
interface MatchApiService {

    /**
     * Obtiene todos los matches disponibles para un usuario.
     *
     * @param userId ID del usuario que busca matches
     * @return Response que contiene una lista de MatchDto
     *
     * Cómo funciona:
     * 1. RemoteDataSource llama: matchApiService.getMatches(userId = 1)
     * 2. Retrofit construye la URL: GET http://10.0.2.2:3000/matches?userId=1
     * 3. OkHttpClient suma el LoggingInterceptor para debug
     * 4. Envía la solicitud HTTP al servidor
     * 5. El servidor (Mockoon) responde con JSON
     * 6. Gson parsea el JSON a List<MatchDto>
     * 7. Retrofit envuelve el resultado en Response<List<MatchDto>>
     * 8. RemoteDataSource desenvuelve con response.body() o captura el error
     */
    @GET("matches")
    suspend fun getMatches(
        @Query("userId") userId: Long
    ): Response<List<MatchDto>>

    /**
     * Obtiene un match específico por su ID.
     *
     * @param matchId ID del match a obtener
     * @return Response que contiene un solo MatchDto
     *
     * Anotación @Path vs @Query:
     * - @Path sustituye un segmento de la URL: /matches/{matchId} → /matches/5
     * - @Query añade un parámetro en la query string: ?userId=1&status=ACCEPTED
     *
     * URL generada: GET http://10.0.2.2:3000/matches/5
     */
    @GET("matches/{matchId}")
    suspend fun getMatchById(
        @Path("matchId") matchId: Long
    ): Response<MatchDto>

    /**
     * Obtiene todos los matches de un usuario con un estado específico.
     *
     * @param userId ID del usuario
     * @param status Estado del match (ej: "PENDING", "ACCEPTED", "REJECTED")
     * @return Response que contiene lista filtrada de MatchDto
     *
     * URL generada: GET http://10.0.2.2:3000/matches?userId=1&status=ACCEPTED
     *
     * Nota: tenemos TWO @Query aquí. Retrofit los concatena automáticamente
     * separados por & en la query string.
     */
    @GET("matches")
    suspend fun getMatchesByStatus(
        @Query("userId") userId: Long,
        @Query("status") status: String
    ): Response<List<MatchDto>>

    /**
     * Obtiene los datos compactos para la lista de Home.
     *
     * Este endpoint alimenta la única lista principal implementada hoy:
     * Match Summary cards.
     */
    @GET("match-summaries")
    suspend fun getMatchSummaries(
        @Query("userId") userId: Long
    ): Response<List<MatchSummaryDto>>

    /**
     * En el futuro añadiremos más métodos aquí:
     * - @POST para crear matches
     * - @PUT para actualizar matches
     * - @DELETE para cancelar matches
     *
     * Por ahora, empezamos con solo GETs para leer datos del mock.
     */
}
