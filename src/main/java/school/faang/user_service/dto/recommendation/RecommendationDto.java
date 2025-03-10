package school.faang.user_service.dto.recommendation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class RecommendationDto {
    @Nullable
    Long id;
    @NonNull
    Long authorId;
    @NonNull
    Long receiverId;
    @NonNull
    String content;
    @Nullable
    List<SkillOfferDto> skillOffers;
    @Nullable
    LocalDateTime createdAt;
}
