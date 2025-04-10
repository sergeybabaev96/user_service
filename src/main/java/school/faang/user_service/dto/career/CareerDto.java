package school.faang.user_service.dto.career;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CareerDto {

    @Min(value = 1, message = "id Карьеры не может быть )")
    private Long id;

    @NotNull
    @JsonFormat(pattern =  "yyyy-MM-dd HH:mm:ss")
    private LocalDate from;

    @NotNull
    @JsonFormat(pattern =  "yyyy-MM-dd HH:mm:ss")
    private LocalDate to;

    @NotBlank(message = "название компания не может быть пустым")
    private String company;

    @NotBlank(message = "название Позиции не может быть  пустым")
    private String position;

}
