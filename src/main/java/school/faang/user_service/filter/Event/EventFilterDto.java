package school.faang.user_service.filter.Event;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDto {
    private String title;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @FutureOrPresent(message = "End date cannot be in the past")
    private LocalDateTime endDate;

    @NotNull(message = "Owner ID cannot be null")
    @Min(value = 1, message = "Owner ID must be a positive number")
    private Long ownerId;

}
