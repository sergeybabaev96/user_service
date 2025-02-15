package school.faang.user_service.dto.kafka;

public record UserProfileViewedDto(
        Long viewerId,
        Long profileOwnerId
) {}
