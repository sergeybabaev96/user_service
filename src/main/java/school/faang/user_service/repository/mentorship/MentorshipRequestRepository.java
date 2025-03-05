package school.faang.user_service.repository.mentorship;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Optional;

public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequest, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO mentorship_request (
            description, requester_id, receiver_id, status, rejection_reason, created_at, updated_at)
            VALUES (
            :description, :requesterId, :receiverId, 0, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING *
            """
    )
    MentorshipRequest create(
            @Param("description") String description,
            @Param("requesterId") Long requesterId,
            @Param("receiverId") Long receiverId
    );

    @Query(nativeQuery = true, value = """
            SELECT * FROM mentorship_request
            WHERE requester_id = :requesterId AND receiver_id = :receiverId
            ORDER BY created_at DESC
            LIMIT 1
            """
    )
    Optional<MentorshipRequest> findLatestRequest(long requesterId, long receiverId);
}