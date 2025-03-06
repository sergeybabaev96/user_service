package school.faang.user_service.repository.promotion;

import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.promotion.user.UserPromotionCount;

public interface UserPromotionCountRepository extends CrudRepository<UserPromotionCount, Long> {
}
