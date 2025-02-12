package school.faang.user_service.dto.event;

import lombok.Builder;

@Builder
public record ParticipantsCountDto(Long eventId, long participantsCount) {
}
