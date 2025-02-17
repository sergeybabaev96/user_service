package school.faang.user_service.dto;

public record BuyTariffRequest(
        TariffDto tariffDto,
        Long id
) {
}
