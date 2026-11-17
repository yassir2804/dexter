package com.dexter.backend.service

import com.dexter.backend.dto.ProfileSummaryResult

interface ProfileService {

    /**
     * Busca el perfil completo de un usuario (datos, skills, reviews).
     * @throws NoSuchElementException si el user no existe → 404.
     */
    fun findByUserId(userId: Long): ProfileSummaryResult

    /**
     * Actualiza campos editables (bio, country, avatarUrl) del perfil.
     * @return el perfil actualizado.
     * @throws NoSuchElementException si el user no existe → 404.
     */
    fun update(userId: Long, dto: ProfileSummaryResult): ProfileSummaryResult
}
