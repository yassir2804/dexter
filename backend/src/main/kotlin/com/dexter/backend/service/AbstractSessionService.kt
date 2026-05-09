package com.dexter.backend.service

import com.dexter.backend.dto.SessionDetails
import com.dexter.backend.dto.SessionRequest
import com.dexter.backend.entity.Session
import com.dexter.backend.entity.SessionStatus
import com.dexter.backend.repository.MatchRepository
import com.dexter.backend.repository.SessionRepository
import com.dexter.backend.repository.SkillRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AbstractSessionService(
    @Autowired val sessionRepository: SessionRepository,
    @Autowired val matchRepository: MatchRepository,
    @Autowired val skillRepository: SkillRepository,
) : SessionService {

    override fun findByMatch(matchId: Long): List<SessionDetails> =
        sessionRepository.findByMatchIdOrderBySessionNumberAsc(matchId).map(::toDetails)

    override fun findById(id: Long): SessionDetails =
        toDetails(sessionRepository.findById(id).orElseThrow {
            NoSuchElementException("Session with id $id not found")
        })

    override fun create(request: SessionRequest): SessionDetails {
        val matchId = request.matchId ?: throw IllegalArgumentException("matchId is required")
        val skillId = request.skillId ?: throw IllegalArgumentException("skillId is required")
        val sessionNumber = request.sessionNumber
            ?: throw IllegalArgumentException("sessionNumber is required")
        val title = request.title ?: throw IllegalArgumentException("title is required")

        val match = matchRepository.findById(matchId).orElseThrow {
            NoSuchElementException("Match with id $matchId not found")
        }
        val skill = skillRepository.findById(skillId).orElseThrow {
            NoSuchElementException("Skill with id $skillId not found")
        }
        val status = parseStatus(request.status) ?: SessionStatus.SCHEDULED

        val saved = sessionRepository.save(
            Session(
                match = match,
                skill = skill,
                sessionNumber = sessionNumber,
                title = title,
                description = request.description,
                status = status,
                scheduledAt = request.scheduledAt,
                completedAt = request.completedAt,
                createdAt = Date(),
            )
        )
        return toDetails(saved)
    }

    override fun update(id: Long, request: SessionRequest): SessionDetails {
        val session = sessionRepository.findById(id).orElseThrow {
            NoSuchElementException("Session with id $id not found")
        }
        request.title?.let { session.title = it }
        request.description?.let { session.description = it }
        request.sessionNumber?.let { session.sessionNumber = it }
        request.scheduledAt?.let { session.scheduledAt = it }
        request.completedAt?.let { session.completedAt = it }
        parseStatus(request.status)?.let { session.status = it }
        request.skillId?.let { skillId ->
            session.skill = skillRepository.findById(skillId).orElseThrow {
                NoSuchElementException("Skill with id $skillId not found")
            }
        }
        return toDetails(sessionRepository.save(session))
    }

    override fun complete(id: Long): SessionDetails {
        val session = sessionRepository.findById(id).orElseThrow {
            NoSuchElementException("Session with id $id not found")
        }
        session.status = SessionStatus.COMPLETED
        session.completedAt = Date()
        return toDetails(sessionRepository.save(session))
    }

    override fun delete(id: Long) {
        if (!sessionRepository.existsById(id)) {
            throw NoSuchElementException("Session with id $id not found")
        }
        sessionRepository.deleteById(id)
    }

    private fun parseStatus(s: String?): SessionStatus? = s?.let {
        try { SessionStatus.valueOf(it) }
        catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid status: $it (expected SCHEDULED|COMPLETED|CANCELLED)")
        }
    }

    private fun toDetails(s: Session): SessionDetails = SessionDetails(
        id = s.id,
        matchId = s.match?.id,
        skillId = s.skill?.id,
        skillName = s.skill?.name,
        sessionNumber = s.sessionNumber,
        title = s.title,
        description = s.description,
        status = s.status.name,
        scheduledAt = s.scheduledAt?.time,
        completedAt = s.completedAt?.time,
        createdAt = s.createdAt?.time,
    )
}
