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
@EqualsAndHashCode(exclude = {"id",
        "isActive",
        "expirePeriod",
        "paymentId"})
public class TariffDto {
    private Long id;
    private String plan;
    private Integer shows;
    private Integer priority;
    private Boolean isActive;
    private LocalDateTime expirePeriod;
    private Long paymentId;
}
