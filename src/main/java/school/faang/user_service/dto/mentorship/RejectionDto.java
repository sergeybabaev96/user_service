package school.faang.user_service.dto.mentorship;

import lombok.Data;

@Data
public class RejectionDto {
    private Long requesterId;
    private Long receiverId;
    private String rejectionReason;;
}
