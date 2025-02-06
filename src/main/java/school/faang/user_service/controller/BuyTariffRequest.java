package school.faang.user_service.controller;

import school.faang.user_service.dto.TariffDto;

public record BuyTariffRequest (
        TariffDto tariffDto,
        Long id
) {
}
