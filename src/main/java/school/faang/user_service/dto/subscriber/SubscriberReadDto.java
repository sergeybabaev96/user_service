package school.faang.user_service.dto.subscriber;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubscriberReadDto {
    Long id;
    String username;
    String email;
}