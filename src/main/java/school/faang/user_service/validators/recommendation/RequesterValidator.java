package school.faang.user_service.validators.recommendation;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.validators.DtoValidator;

@Slf4j
public class RequesterValidator implements DtoValidator<RecommendationRequestDto> {
    public static final String REQUESTER_ID_IS_EMPTY = "recommendation requester id is empty or 0";
    @Override
    public void validate(RecommendationRequestDto dto) {
        if (dto.getRequesterId() == null || dto.getRequesterId().compareTo(1L) < 0) {
            log.error("RecommendationRequestDto: {}, error: {}", dto, REQUESTER_ID_IS_EMPTY);
            throw new IllegalArgumentException(REQUESTER_ID_IS_EMPTY);
        }
    }
}
