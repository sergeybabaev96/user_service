package school.faang.user_service.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

@Getter
@Setter
public class CareerDto {
    private long id;
    private LocalDate from;
    private LocalDate to;
    private String company;
    private String position;
}
