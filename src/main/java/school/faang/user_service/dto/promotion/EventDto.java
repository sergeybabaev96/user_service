package school.faang.user_service.dto.promotion;

import java.time.LocalDateTime;

public record EventDto(
        Long id,
        Long eventId,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String location,
        UserDto userDto
) {
}
