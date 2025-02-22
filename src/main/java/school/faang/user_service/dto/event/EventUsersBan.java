package school.faang.user_service.dto.event;

import lombok.Builder;

import java.util.List;

@Builder
public record EventUsersBan(
        List<Long> userIdsToBan
) {
}
