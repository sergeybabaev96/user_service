package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

public interface SkillRequestRepository extends CrudRepository<SkillRequest, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO skill_request (request_id, skill_id)
            VALUES (:requestId, :skillId)
            """)
    @Modifying
    @Transactional
    void create(long requestId, long skillId);

    List<SkillRequest> findAllByRequestId(long requestId);
}