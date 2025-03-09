package school.faang.user_service.dto.recommendation;

import org.jetbrains.annotations.Nullable;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public record RequestFilterDto(
        @Nullable String requesterNamePattern,
        @Nullable String receiverNamePattern,
        @Nullable String messagePattern,
        Optional<RequestStatus> status,
        @Nullable String rejectionReasonPattern,
        @Nullable LocalDateTime createdAtFrom,
        @Nullable LocalDateTime createdAtTo,
        @Nullable LocalDateTime updatedAtFrom,
        @Nullable LocalDateTime updatedAtTo) {
}
