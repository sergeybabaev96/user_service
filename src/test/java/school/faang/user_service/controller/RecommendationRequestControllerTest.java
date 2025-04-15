package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestControllerTest {
    @InjectMocks
    RecommendationRequestController controller;
    @Mock
    RecommendationRequestService service;

    @Test
    void testPositiveRequestRecommendation() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setMessage("not is empty");
        controller.requestRecommendation(dto);
        verify(service, times(1)).create(dto);
    }

    @Test
    void testPositiveGetRecommendationRequests() {
        RequestFilterDto filter = new RequestFilterDto();
        controller.getRecommendationRequests(filter);
        verify(service, times(1)).getFilteredRecommendationRequests(filter);
    }

    @Test
    void testPositiveGetRecommendationRequest() {
        long id = 1L;
        controller.getRecommendationRequest(id);
        verify(service, times(1)).getRecommendationRequestById(id);
    }

    @Test
    void testPositiverejectRequest() {
        long id = 1L;
        RejectionDto rejectionDtodto = new RejectionDto();
        controller.rejectRequest(id, rejectionDtodto);
        verify(service, times(1)).rejectRequest(id, rejectionDtodto);
    }

    @Test
    void testNegativeRequestRecommendationDtoMessageIsNull() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        assertThrows(IllegalArgumentException.class, ()
                -> controller.requestRecommendation(dto));

    }

    @Test
    void testNegativeRequestRecommendationDtoMessageIsEmpty() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setMessage("");
        assertThrows(IllegalArgumentException.class, ()
                -> controller.requestRecommendation(dto));
    }
}
