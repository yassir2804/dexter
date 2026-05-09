package com.dexter.backend.service

import com.dexter.backend.dto.SessionInvitationDetails
import com.dexter.backend.dto.SessionInvitationRequest
import com.dexter.backend.entity.InvitationStatus
import com.dexter.backend.entity.SessionInvitation
import com.dexter.backend.repository.MatchRepository
import com.dexter.backend.repository.SessionInvitationRepository
import com.dexter.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AbstractSessionInvitationService(
    @Autowired val invitationRepository: SessionInvitationRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val matchRepository: MatchRepository,
) : SessionInvitationService {

    override fun findByReceiver(receiverId: Long): List<SessionInvitationDetails> =
        invitationRepository.findByReceiverId(receiverId).map(::toDetails)

    override fun findById(id: Long): SessionInvitationDetails =
        toDetails(invitationRepository.findById(id).orElseThrow {
            NoSuchElementException("SessionInvitation with id $id not found")
        })

    override fun create(request: SessionInvitationRequest): SessionInvitationDetails {
        val senderId = request.senderId ?: throw IllegalArgumentException("senderId is required")
        val receiverId = request.receiverId ?: throw IllegalArgumentException("receiverId is required")
        val matchId = request.matchId ?: throw IllegalArgumentException("matchId is required")

        val sender = userRepository.findById(senderId).orElseThrow {
            NoSuchElementException("Sender with id $senderId not found")
        }
        val receiver = userRepository.findById(receiverId).orElseThrow {
            NoSuchElementException("Receiver with id $receiverId not found")
        }
        val match = matchRepository.findById(matchId).orElseThrow {
            NoSuchElementException("Match with id $matchId not found")
        }

        val saved = invitationRepository.save(
            SessionInvitation(
                sender = sender,
                receiver = receiver,
                match = match,
                status = InvitationStatus.PENDING,
                expiresAt = request.expiresAt,
                createdAt = Date(),
            )
        )
        return toDetails(saved)
    }

    override fun accept(id: Long): SessionInvitationDetails = transition(id, InvitationStatus.ACCEPTED)
    override fun reject(id: Long): SessionInvitationDetails = transition(id, InvitationStatus.REJECTED)
    override fun cancel(id: Long): SessionInvitationDetails = transition(id, InvitationStatus.EXPIRED)

    private fun transition(id: Long, newStatus: InvitationStatus): SessionInvitationDetails {
        val inv = invitationRepository.findById(id).orElseThrow {
            NoSuchElementException("SessionInvitation with id $id not found")
        }
        inv.status = newStatus
        return toDetails(invitationRepository.save(inv))
    }

    private fun toDetails(i: SessionInvitation): SessionInvitationDetails = SessionInvitationDetails(
        id = i.id,
        senderId = i.sender?.id,
        senderName = "${i.sender?.firstName ?: ""} ${i.sender?.lastName ?: ""}".trim(),
        receiverId = i.receiver?.id,
        receiverName = "${i.receiver?.firstName ?: ""} ${i.receiver?.lastName ?: ""}".trim(),
        matchId = i.match?.id,
        status = i.status.name,
        expiresAt = i.expiresAt?.time,
        createdAt = i.createdAt?.time,
    )
}
