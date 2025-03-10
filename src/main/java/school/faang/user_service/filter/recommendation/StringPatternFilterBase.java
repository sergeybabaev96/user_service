package school.faang.user_service.filter.recommendation;

import jakarta.annotation.Nullable;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class StringPatternFilterBase implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        var patternString = getPattern(filterDto);
        return patternString != null && !patternString.isBlank();
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> source, RequestFilterDto filterDto) {
        var patternString = getPattern(filterDto);
        if (patternString == null) {
            return source;
        }

        var pattern = Pattern.compile(patternString);

        return source.filter(x -> {
            var field = getFieldToCheck(x);
            return field != null && pattern.matcher(field).matches();
        });
    }

    protected abstract String getFieldToCheck(RecommendationRequest recommendationRequest);

    @Nullable
    protected abstract String getPattern(RequestFilterDto filterDto);
}
