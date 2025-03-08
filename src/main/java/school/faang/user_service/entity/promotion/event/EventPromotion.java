package school.faang.user_service.entity.promotion.event;

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
@Table(name = "event_promotion")
public class EventPromotion {
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

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "percentage", nullable = false)
    private Integer percentage;
}
