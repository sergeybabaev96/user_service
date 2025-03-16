package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RequestFilterDto {
    private RequestStatus status;
}
