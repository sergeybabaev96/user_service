package school.faang.user_service.dto.subscription;

import lombok.Builder;

@Builder
public record SubscriptionUserDto(

        Long id,

        String username,

        String email
) {
}
