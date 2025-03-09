package school.faang.user_service.filter;

import org.jetbrains.annotations.Nullable;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.recommendation.RequestFilterDto;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RecommendationRequestFilter implements Predicate<RecommendationRequest> {
    @Nullable
    private final Pattern requesterNamePattern;
    @Nullable
    private final Pattern receiverNamePattern;
    @Nullable
    private final Pattern messagePattern;
    private final Optional<RequestStatus> status;
    @Nullable
    private final Pattern rejectionReasonPattern;
    @Nullable
    private final LocalDateTime createdAtFrom;
    @Nullable
    private final LocalDateTime createdAtTo;
    @Nullable
    private final LocalDateTime updatedAtFrom;
    @Nullable
    private final LocalDateTime updatedAtTo;

    public RecommendationRequestFilter(RequestFilterDto filterDto) {
        requesterNamePattern = createPattern(filterDto.requesterNamePattern());
        receiverNamePattern = createPattern(filterDto.receiverNamePattern());
        messagePattern = createPattern(filterDto.messagePattern());
        rejectionReasonPattern = createPattern(filterDto.rejectionReasonPattern());

        status = filterDto.status();
        createdAtFrom = filterDto.createdAtFrom();
        createdAtTo = filterDto.createdAtTo();
        updatedAtFrom = filterDto.updatedAtFrom();
        updatedAtTo = filterDto.updatedAtTo();
    }

    @Override
    public boolean test(@Nullable RecommendationRequest recommendationRequest) {
        if (recommendationRequest == null) {
            return false;
        }

        var matchesRequesterName = (requesterNamePattern == null)
                || requesterNamePattern.matcher(recommendationRequest.getRequester().getUsername()).matches();
        var matchesReceiverName = (receiverNamePattern == null)
                || receiverNamePattern.matcher(recommendationRequest.getReceiver().getUsername()).matches();
        var matchesMessage = (messagePattern == null)
                || messagePattern.matcher(recommendationRequest.getMessage()).matches();
        var matchesRejectionReasonPattern = (rejectionReasonPattern == null)
                || rejectionReasonPattern.matcher(recommendationRequest.getRejectionReason()).matches();

        var matchesStatus = status.isPresent() && status.get() == recommendationRequest.getStatus();
        var matchesCreatedAtFrom = isMatchesFromTimestamp(createdAtFrom, recommendationRequest.getCreatedAt());
        var matchesCreatedAtTo = isMatchesToTimestamp(createdAtTo, recommendationRequest.getCreatedAt());
        var matchesUpdatedAtFrom = isMatchesFromTimestamp(updatedAtFrom, recommendationRequest.getUpdatedAt());
        var matchesUpdatedAtTo = isMatchesToTimestamp(updatedAtTo, recommendationRequest.getUpdatedAt());

        return matchesRequesterName
                && matchesReceiverName
                && matchesMessage
                && matchesRejectionReasonPattern
                && matchesStatus
                && matchesCreatedAtFrom
                && matchesCreatedAtTo
                && matchesUpdatedAtFrom
                && matchesUpdatedAtTo;
    }

    @Nullable
    private static Pattern createPattern(@Nullable String pattern) {
        return pattern == null || pattern.isBlank()
                ? null
                : Pattern.compile(pattern);
    }

    private boolean isMatchesFromTimestamp(@Nullable LocalDateTime fromTimestamp, LocalDateTime timestampToCheck) {
        return fromTimestamp == null
                || fromTimestamp.isEqual(timestampToCheck)
                || fromTimestamp.isBefore(timestampToCheck);
    }

    private boolean isMatchesToTimestamp(@Nullable LocalDateTime toTimestamp, LocalDateTime timestampToCheck) {
        return toTimestamp == null
                || toTimestamp.isEqual(timestampToCheck)
                || toTimestamp.isAfter(timestampToCheck);
    }
}
