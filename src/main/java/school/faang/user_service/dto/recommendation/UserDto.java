package school.faang.user_service.dto.recommendation;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private int id;
    private List<RecommendationDto> recommendationsReceived;
}
