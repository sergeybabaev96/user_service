package school.faang.user_service.controller.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationRequestController.class)
@ContextConfiguration(classes = {RecommendationRequestController.class, GlobalExceptionHandler.class})
class RecommendationRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecommendationRequestService recommendationRequestService;

    private final RecommendationRequestDto requestDto = new RecommendationRequestDto();

    private final RejectionDto rejectionDto = new RejectionDto();

    @BeforeEach
    void setUp() {
        requestDto.setId(1L);
        requestDto.setRequesterId(2L);
        requestDto.setReceiverId(3L);
        requestDto.setMessage("Please recommend me");
        requestDto.setStatus(RequestStatus.PENDING);
        rejectionDto.setReason("PENDING");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testRequestRecommendation() throws Exception {
        when(recommendationRequestService.requestRecommendation(any()))
                .thenReturn(requestDto);

        mockMvc.perform(post("/recommendations")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.message").value("Please recommend me"));
    }

    @Test
    void testGetRecommendationRequests() throws Exception {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setRequesterId(2L);
        filter.setReceiverId(3L);
        filter.setStatus(RequestStatus.PENDING);

        List<RecommendationRequestDto> requests = List.of(requestDto);

        when(recommendationRequestService.getRecommendationRequests(any()))
                .thenReturn(requests);

        mockMvc.perform(get("/recommendations/filters")
                        .param("requesterId", "2")
                        .param("receiverId", "3")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testGetRecommendationRequest() throws Exception {

        when(recommendationRequestService.getRecommendationRequest(1L))
                .thenReturn(requestDto);

        mockMvc.perform(get("/recommendations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testRejectRequest() throws Exception {
        requestDto.setStatus(RequestStatus.REJECTED);

        when(recommendationRequestService.rejectRequest(1L, rejectionDto))
                .thenReturn(requestDto);

        mockMvc.perform(put("/recommendations/1/reject")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(rejectionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }
}