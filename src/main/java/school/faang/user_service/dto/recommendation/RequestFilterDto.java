package school.faang.user_service.dto.recommendation;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequestFilterDto {
    @Nullable
    private String requesterNamePattern;
    @Nullable
    private String receiverNamePattern;
    @Nullable
    private String messagePattern;
    @Nullable
    private RequestStatus status;
    @Nullable
    private String rejectionReason;
    @Nullable
    private LocalDateTime createdAtFrom;
    @Nullable
    private LocalDateTime createdAtTo;
    @Nullable
    private LocalDateTime updatedAtFrom;
    @Nullable
    private LocalDateTime updatedAtTo;
}
