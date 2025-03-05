package school.faang.user_service.dto.mentorship;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MentorshipRequestDto {
    private long id;
    private String description;
    private long requester;
    private long receiver;
    //private RequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}