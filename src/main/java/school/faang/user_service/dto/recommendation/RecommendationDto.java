package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDto {

    private Long id;

    @NotNull(message = "The authorId field cannot be null!")
    private Long authorId;

    @NotNull(message = "The receiverId field cannot be null!")
    private Long receiverId;

    @NotBlank(message = "The content field cannot be null!")
    private String content;

    private List<SkillOfferDto> skillOffers;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = Objects.requireNonNullElseGet(createdAt, LocalDateTime::now);
    }

}