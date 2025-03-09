package school.faang.user_service.dto.recommendation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class RecommendationRequestDto {
    @Nullable
    private Long id;
    @NonNull
    private String message;
    private RequestStatus status;
    @Nullable
    private List<SkillRequestDto> skills;
    @NonNull
    private long requesterId;
    @NonNull
    private long receiverId;
    @Nullable
    private LocalDateTime createdAt;
    @Nullable
    private LocalDateTime updatedAt;
}
