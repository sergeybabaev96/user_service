package school.faang.user_service.repository.promotion;

import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.promotion.event.EventPromotionCount;

public interface EventPromotionCountRepository extends CrudRepository<EventPromotionCount, Long> {
}
