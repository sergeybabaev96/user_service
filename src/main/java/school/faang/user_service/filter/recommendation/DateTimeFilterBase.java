package school.faang.user_service.filter.recommendation;

import jakarta.annotation.Nullable;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public abstract class DateTimeFilterBase implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return getFromTimeStamp(filterDto) != null || getToTimeStamp(filterDto) != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> source, RequestFilterDto filterDto) {
        return source.filter(x -> {
            var timestamp = getTimeStamp(x);
            return isMatchesFromTimestamp(getFromTimeStamp(filterDto), timestamp)
                    && isMatchesToTimestamp(getToTimeStamp(filterDto), x.getCreatedAt());
        });
    }

    @Nullable
    protected abstract LocalDateTime getTimeStamp(RecommendationRequest recommendationRequest);

    @Nullable
    protected abstract LocalDateTime getToTimeStamp(RequestFilterDto filterDto);

    @Nullable
    protected abstract LocalDateTime getFromTimeStamp(RequestFilterDto filterDto);

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
