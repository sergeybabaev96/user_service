package school.faang.user_service.filter.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDto {

    @NotNull(message = "Owner not be null")
    private Long ownerId;

    private String location;
}