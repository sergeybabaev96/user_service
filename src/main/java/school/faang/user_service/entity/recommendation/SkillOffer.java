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
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "skill_offer")
public class SkillOffer extends Identifiable {

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    public Skill skill;

    @ManyToOne
    @JoinColumn(name = "recommendation_id", nullable = false)
    private Recommendation recommendation;
}