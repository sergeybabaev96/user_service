package school.faang.user_service.entity.recommendation;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import school.faang.user_service.entity.Identifiable;
import school.faang.user_service.entity.Skill;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skill_request")
@SuperBuilder
public class SkillRequest extends Identifiable {

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private RecommendationRequest request;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}