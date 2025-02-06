package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TariffDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String plan;
    private Integer shows;
    private Integer priority;
    @EqualsAndHashCode.Exclude
    private Boolean isActive;
    @EqualsAndHashCode.Exclude
    private LocalDateTime expirePeriod;
    @EqualsAndHashCode.Exclude
    private Long userId;
    @EqualsAndHashCode.Exclude
    private Long eventId;
    @EqualsAndHashCode.Exclude
    private Long paymentId;
}
