package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EventDto {
    @Min(value = 1, message = "Id must be greater than 0")
    private Long id;
    @Size(max = 64, message = "The length must not exceed 64 characters")
    private String name;
    @Size(max = 255, message = "The length must not exceed 255 characters")
    private String description;
    @Size(max = 128, message = "The length must not exceed 128 characters")
    private String location;
}
