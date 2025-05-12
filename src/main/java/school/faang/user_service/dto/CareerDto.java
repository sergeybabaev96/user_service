package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CareerDto {
    private long id;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String company;
    private String position;
}
