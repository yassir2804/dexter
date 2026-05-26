package com.dexter.app.domain.repository

import com.dexter.app.domain.model.ReviewTag

/**
 * Repository contract for review tags.
 *
 * El backend ya no expone creacion/borrado individual de tags — se crean
 * al postear el Review con su lista de tags. Por eso aqui solo hay
 * operaciones de lectura.
 */
interface ReviewTagRepository {
    /** Lista de tags sugeridos por el backend para el picker (Paciente, Puntual...). */
    suspend fun getCatalog(): Result<List<String>>

    /** Tags persistidos asociados a un review especifico. */
    suspend fun getTagsForReview(reviewId: Long): Result<List<ReviewTag>>
}
