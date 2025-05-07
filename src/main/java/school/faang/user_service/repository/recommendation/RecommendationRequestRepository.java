package school.faang.user_service.repository.recommendation;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Optional;

public interface RecommendationRequestRepository extends JpaRepository<RecommendationRequest, Long> {

    @Transactional
    @Query(nativeQuery = true, value = """
            INSERT INTO recommendation_request 
                (id, message, requester_id, receiver_id, status, rejection_reason, recommendation_id, created_at, updated_at)
            VALUES 
                (:#{#recReq.requestId}, :#{#recReq.message}, :#{#recReq.requesterId}, 
                 :#{#recReq.receiverId}, :#{#recReq.status}, :#{#recReq.rejectionReason}, 
                 :#{#recReq.recommendationId}, :#{#recReq.createdAt}, :#{#recReq.updatedAt})
            """)
    @Modifying
    void create(@Param("recReq") RecommendationRequest recReq);

    @Query(nativeQuery = true, value = """
            SELECT * FROM recommendation_request
            WHERE requester_id = ?1 AND receiver_id = ?2 AND status = 1
            ORDER BY created_at DESC
            LIMIT 1
            """)
    Optional<RecommendationRequest> findLatestPendingRequest(long requesterId, long receiverId);

}