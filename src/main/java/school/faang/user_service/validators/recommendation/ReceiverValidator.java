package school.faang.user_service.validators.recommendation;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.validators.DtoValidator;

@Slf4j
public class ReceiverValidator implements DtoValidator<RecommendationRequestDto> {
    public static final String RECEIVER_ID_IS_EMPTY = "recommendation receiver id is empty or 0";
    @Override
    public void validate(RecommendationRequestDto dto) {
        if (dto.getReceiverId() == null || dto.getReceiverId().compareTo(1L) < 0) {
            log.error("RecommendationRequestDto: {}, error: {}", dto, RECEIVER_ID_IS_EMPTY);
            throw new IllegalArgumentException(RECEIVER_ID_IS_EMPTY);
        }
    }
}
