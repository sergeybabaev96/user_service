package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.filter.recommendation.TestRecommendationRequestAcceptedFilterStutus;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    private static final LocalDateTime SIX_MONTHS_AGO = LocalDateTime.now().minusMonths(6);

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    private RecommendationRequestFilter filter = new TestRecommendationRequestAcceptedFilterStutus();
    @Mock
    private UserService userService;
    @Mock
    private SkillRequestService skillRequestService;
    private RecommendationRequestService recommendationRequestService;
    private RecommendationRequest firstRecommendationRequest;
    private RecommendationRequest secondRecommendationRequest;
    private RecommendationRequestDto requestDto;
    private User requester;
    private User receiver;
    private RecommendationRequest request;

    @BeforeEach
    public void setUp() {
        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository,
                recommendationRequestMapper, List.of(filter), userService, skillRequestService);
    }

    @BeforeEach
    public void init() {
        firstRecommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .build();
        secondRecommendationRequest = RecommendationRequest.builder()
                .id(2L)
                .status(RequestStatus.ACCEPTED)
                .build();

        requester = new User();
        requester.setId(1L);

        receiver = new User();
        receiver.setId(2L);

        requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(requester.getId());
        requestDto.setReceiverId(receiver.getId());
        requestDto.setMessage("Please recommend me!");
        requestDto.setSkillsId(Collections.singletonList(3L));

        request = new RecommendationRequest();
        request.setId(1L);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setMessage(requestDto.getMessage());
        request.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testGetFilteredRecommendationRequestsAcceptedIsFiltered() {

        when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(firstRecommendationRequest, secondRecommendationRequest));

        List<RecommendationRequestDto> requestDtos = recommendationRequestService
                .getFilteredRecommendationRequests(new RequestFilterDto(null));

        assertFalse(requestDtos.isEmpty(), "Expected the filtered list to be empty");
        assertEquals(1, requestDtos.size());
    }

    @Test
    public void testGetFilteredRecommendationRequestsAcceptedIsNotFiltered() {
        firstRecommendationRequest.setStatus(RequestStatus.PENDING);
        secondRecommendationRequest.setStatus(RequestStatus.PENDING);
        // Настраиваем моки
        when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(firstRecommendationRequest, secondRecommendationRequest));
        // Act: вызываем метод, который мы тестируем
        List<RecommendationRequestDto> requestDtos = recommendationRequestService
                .getFilteredRecommendationRequests(new RequestFilterDto(null));
        // Assert: проверяем, что возвращаемый список не пуст
        assertTrue(requestDtos.isEmpty(), "Expected the filtered list to be empty");
        assertEquals(0, requestDtos.size());
    }

    @Test
    public void testGetRecommendationRequestById() {
        long id = 1L;
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(id);
        RecommendationRequest request1 = new RecommendationRequest();
        request1.setId(id);

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(request1));

        RecommendationRequestDto dto1 = recommendationRequestService.getRecommendationRequestById(id);
        assertEquals(dto.getId(), dto1.getId());
        assertNotNull(dto1);

    }

    @Test
    public void testGetRecommendationRequestByIdNotFound() {
        long id = 1L;
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(id);
        RecommendationRequest request1 = new RecommendationRequest();
        request1.setId(id);

        when(recommendationRequestRepository.findById(id)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> recommendationRequestService.getRecommendationRequestById(id));

    }

    @Test
    public void testRejectRequestStatusNotPending() {
        long requestId = 1L;
        RecommendationRequest request = new RecommendationRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.ACCEPTED);
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("запрос отклонен");

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(IllegalStateException.class, () -> recommendationRequestService.rejectRequest(requestId, rejectionDto));
    }

    @Test
    public void testRejectRequestStatusAccepted() {
        long requestId = 1L;
        RecommendationRequest request = new RecommendationRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("запрос отклонен");

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        RecommendationRequestDto dto = recommendationRequestService
                .rejectRequest(requestId, rejectionDto);

        verify(recommendationRequestRepository, times(1)).save(request);
        assertEquals(RequestStatus.REJECTED, request.getStatus());
        assertEquals("запрос отклонен", request.getRejectionReason());
        assertEquals(dto.getId(), request.getId());
    }

    @Test
    public void testRequesterAndReceiverAndCreatedDateAfterIsNot() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setMessage("Please recommend me!");

        User requester = new User();
        User receiver = new User();

        when(userService.getUserById(1L)).thenReturn(requester);
        when(userService.getUserById(2L)).thenReturn(receiver);
        when(recommendationRequestRepository.findByRequesterAndReceiverAndCreatedDateAfter(any(User.class),
                any(User.class), any(LocalDateTime.class))).thenReturn(Optional.of(request));

        assertThrows(IllegalStateException.class, () -> recommendationRequestService.create(dto));
    }
    @Test
    public void testRequesterAndReceiverAndCreatedDateAfter() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setMessage("Please recommend me!");
        dto.setSkillsId(Arrays.asList(1L, 2L));

        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        when(skillRequestService.findByIds(dto.getSkillsId())).thenReturn(new ArrayList<>());
        // Настройка моков для пользователей
        when(userService.getUserById(dto.getRequesterId())).thenReturn(requester);
        when(userService.getUserById(dto.getReceiverId())).thenReturn(receiver);

        // Вызов метода, который мы тестируем
        recommendationRequestService.create(dto);

        // Проверка, что метод save был вызван с правильным объектом
        verify(recommendationRequestRepository, times(1)).save(argThat(savedRequest -> {
            assertEquals(requester, savedRequest.getRequester());
            assertEquals(receiver, savedRequest.getReceiver());
            assertEquals(dto.getMessage(), savedRequest.getMessage());
            return true;
        }));
    }


    @Test
    public void testCreatedDtoNull() {
        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(null));


    }
    @Test
    public void testCreatedDtosetRequesterIdNull() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setReceiverId(3L);
        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(dto));


    }
    @Test
    public void testCreatedRequesterNull() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setReceiverId(3L);
        assertThrows(NotFoundException.class, () -> recommendationRequestService.create(dto));

    }
    @Test
    public void testCreatedtReceiverNull() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setReceiverId(3L);
        assertThrows(NotFoundException.class, () -> recommendationRequestService.create(dto));


    }


}

