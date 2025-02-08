package school.faang.user_service.dto.premium;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PremiumDto(long userId,
                         String userName,
                         LocalDate startDate,
                         LocalDate endDate) {

}