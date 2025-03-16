package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationRequestServiceTest {
    RecommendationRequestService service;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void requestRecommendation() {
        RecommendationRequestDto expected = new RecommendationRequestDto();
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        RecommendationRequestDto actual = service.requestRecommendation(requestDto);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getRecommendationRequests() {
    }

    @Test
    void getRecommendationRequest() {
    }

    @Test
    void rejectRequest() {
    }
}