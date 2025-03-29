package school.faang.user_service.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CareerDto (
    long id,
    LocalDate from,
    LocalDate to,
    @Size(max = 50)
    String company,
    @Size(max = 50)
    String position
    ) {}
