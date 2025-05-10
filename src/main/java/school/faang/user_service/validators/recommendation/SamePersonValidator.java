package school.faang.user_service.validators.recommendation;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.validators.DtoValidator;

@Slf4j
public class SamePersonValidator implements DtoValidator<RecommendationRequestDto> {
    public static final String SAME_PERSON = "Requester and Receiver is a same person";

    @Override
    public void validate(RecommendationRequestDto dto) {
        long requesterId = dto.getRequesterId();
        long receiverId = dto.getReceiverId();
        if (requesterId == receiverId) {
            log.error("RecommendationRequestDto: {}, error: {}", dto, SAME_PERSON);
            throw new IllegalArgumentException(SAME_PERSON);
        }
    }
}
