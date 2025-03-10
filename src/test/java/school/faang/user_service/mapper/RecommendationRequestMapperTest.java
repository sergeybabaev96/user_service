package school.faang.user_service.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RecommendationRequestMapperTest {

    @Spy
    private RecommendationRequestMapper mapper = Mappers.getMapper(RecommendationRequestMapper.class);

    @Test
    @DisplayName("Проверка маппинга из DTO в RecommendationRequest")
    void toRecommendationRequest() {
        // arrange
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(1L);
        dto.setMessage("Hello World");
        dto.setRequesterId(100L);
        dto.setReceiverId(200L);
        dto.setSkills(List.of());
        dto.setStatus(RequestStatus.PENDING);

        // act
        RecommendationRequest request = mapper.toRecommendationRequest(dto);

        // assert
        assertNotNull(request, "Полученный объект не должен быть null");
        assertEquals(dto.getId(), request.getId(), "Проверка идентификатора");
        assertEquals(dto.getMessage(), request.getMessage(), "Проверка сообщения");
        assertNotNull(request.getRequester(), "Объект requester не должен быть null");
        assertEquals(dto.getRequesterId(), request.getRequester().getId(), "Проверка идентификатора requester");
        assertNotNull(request.getReceiver(), "Объект receiver не должен быть null");
        assertEquals(dto.getReceiverId(), request.getReceiver().getId(), "Проверка идентификатора receiver");
        assertEquals(dto.getStatus(), request.getStatus(), "Проверка статуса");
        assertNotNull(request.getSkills(), "Список скиллов не должен быть null");
        assertEquals(dto.getSkills().size(), request.getSkills().size(), "Проверка размера списка скиллов");
    }

    @Test
    void toRecommendationRequestDto() {
    }

    @Test
    void toRecommendationRequestDtoList() {
    }
}