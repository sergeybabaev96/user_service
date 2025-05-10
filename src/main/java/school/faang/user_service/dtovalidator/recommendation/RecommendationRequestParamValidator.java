package school.faang.user_service.dtovalidator.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dtovalidator.BaseParamsValidator;
import school.faang.user_service.exceptions.RecommendationRequestException;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

@Slf4j
@Component
public class RecommendationRequestParamValidator extends BaseParamsValidator<RecommendationRequestDto> {
//    //public static final String REQUESTER_ID_IS_EMPTY = "recommendation requester id is empty or 0";
//    //public static final String RECEIVER_ID_IS_EMPTY = "recommendation receiver id is empty or 0";
//    //public static final String MESSAGE_IS_EMPTY = "recommendation message is empty";
//    public static final String DATE_CREATE_IS_EMPTY = "recommendation date create is empty";
//    public static final String DATE_UPDATE_IS_EMPTY = "recommendation date update is empty";
//    public static final String REQUEST_IS_NULL = "Recommendation request is null";
//    public static final String VALIDATION_ERROR = "Recommendation request validation error: {0}";
//    //public static final String SAME_PERSON = "Requester and Receiver is a same person";

    @Override
    public void validate(RecommendationRequestDto dto) {
//        Predicate<String> stringPredicate = word -> word == null || word.isBlank();
//        Predicate<Long> idPredicate = id -> id == null || id.compareTo(1L) < 0;
//        Predicate<LocalDateTime> datePredicate = Objects::isNull;
//
//        if (dto == null) {
//            log.error(REQUEST_IS_NULL);
//            throw new NoSuchElementException(REQUEST_IS_NULL);
//        }
//
//        try {
//            addParam(dto.getRequesterId(), idPredicate, REQUESTER_ID_IS_EMPTY);
//            addParam(dto.getReceiverId(), idPredicate, RECEIVER_ID_IS_EMPTY);
//            addParam(dto.getMessage(), stringPredicate, MESSAGE_IS_EMPTY);
//            addParam(dto.getCreatedAt(), datePredicate, DATE_CREATE_IS_EMPTY);
//            addParam(dto.getUpdatedAt(), datePredicate, DATE_UPDATE_IS_EMPTY);
//            check(true);
//        } catch (IllegalArgumentException e) {
//            String errorMessage = MessageFormat.format(VALIDATION_ERROR, e.getMessage());
//            log.error(errorMessage, e);
//            throw new RecommendationRequestException(errorMessage);
//        }
//
//        if (dto.getReceiverId().equals(dto.getRequesterId())) {
//            log.error(SAME_PERSON);
//            throw new RecommendationRequestException(SAME_PERSON);
//        }
    }
}
