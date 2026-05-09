package com.dexter.backend.mapper

import com.dexter.backend.dto.SessionDetails
import com.dexter.backend.entity.Session
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface SessionMapper {

    @Mapping(target = "matchId", expression = "java(session.getMatch() != null ? session.getMatch().getId() : null)")
    @Mapping(target = "skillId", expression = "java(session.getSkill() != null ? session.getSkill().getId() : null)")
    @Mapping(target = "skillName", expression = "java(session.getSkill() != null ? session.getSkill().getName() : null)")
    @Mapping(target = "status", expression = "java(session.getStatus().name())")
    @Mapping(target = "scheduledAt", expression = "java(session.getScheduledAt() != null ? session.getScheduledAt().getTime() : null)")
    @Mapping(target = "completedAt", expression = "java(session.getCompletedAt() != null ? session.getCompletedAt().getTime() : null)")
    @Mapping(target = "createdAt", expression = "java(session.getCreatedAt() != null ? session.getCreatedAt().getTime() : null)")
    fun toDetails(session: Session): SessionDetails

    fun toDetailsList(sessions: List<Session>): List<SessionDetails>
}
