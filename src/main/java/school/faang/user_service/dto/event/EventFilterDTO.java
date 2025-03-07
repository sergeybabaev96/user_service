package school.faang.user_service.dto.event;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDTO {
    private String location;
}
