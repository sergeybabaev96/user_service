package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;

public interface EducationRepository extends JpaRepository<Education, Long> {
}
