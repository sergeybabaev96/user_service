package school.faang.user_service.service.tariff;

import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.entity.Tariff;

public interface TariffService {

    Tariff buyTariff(TariffDto tariffDto, Long userId);

    void decrementShows(Tariff tariff);
}
