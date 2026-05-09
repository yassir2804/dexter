package com.dexter.backend.service

import com.dexter.backend.dto.SessionDetails
import com.dexter.backend.dto.SessionRequest
import com.dexter.backend.entity.SessionStatus
import com.dexter.backend.mapper.SessionMapper
import com.dexter.backend.repository.SessionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AbstractSessionService(
    @Autowired val sessionRepository: SessionRepository,
    @Autowired val sessionMapper: SessionMapper,
) : SessionService {

    override fun findByMatch(matchId: Long): List<SessionDetails> =
        sessionMapper.toDetailsList(
            sessionRepository.findByMatchIdOrderBySessionNumberAsc(matchId)
        )

    override fun findById(id: Long): SessionDetails =
        sessionMapper.toDetails(
            sessionRepository.findById(id)
                .orElseThrow { NoSuchElementException("Session $id not found") }
        )

    override fun update(id: Long, request: SessionRequest): SessionDetails {
        val session = sessionRepository.findById(id)
            .orElseThrow { NoSuchElementException("Session $id not found") }

        session.title = request.title
        session.description = request.description
        session.scheduledAt = request.scheduledAt?.let { Date(it) }

        return sessionMapper.toDetails(sessionRepository.save(session))
    }

    override fun complete(id: Long): SessionDetails {
        val session = sessionRepository.findById(id)
            .orElseThrow { NoSuchElementException("Session $id not found") }

        session.status = SessionStatus.COMPLETED
        session.completedAt = Date()

        return sessionMapper.toDetails(sessionRepository.save(session))
    }

    override fun cancel(id: Long) {
        val session = sessionRepository.findById(id)
            .orElseThrow { NoSuchElementException("Session $id not found") }
        session.status = SessionStatus.CANCELLED
        sessionRepository.save(session)
    }
}
