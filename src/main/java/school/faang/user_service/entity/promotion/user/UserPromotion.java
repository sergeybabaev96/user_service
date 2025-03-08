package school.faang.user_service.entity.promotion.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "user_promotion")
public class UserPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @FutureOrPresent
    @Column(name = "start_date", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Future
    @Column(name = "end_date", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "percentage", nullable = false)
    private Integer percentage;
}
