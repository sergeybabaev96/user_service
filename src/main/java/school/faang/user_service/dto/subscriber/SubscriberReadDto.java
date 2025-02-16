package school.faang.user_service.dto.subscriber;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubscriberReadDto {
    private Long id;
    private String username;
    private String email;
}