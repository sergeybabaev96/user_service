package school.faang.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tariff")
public class Tariff extends Identifiable {

    @Column(name = "plan", length = 64, nullable = false, unique = true)
    private String plan;

    @Column(name = "shows", nullable = false)
    private Integer shows;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "active", nullable = false)
    private Boolean isActive;

    @Column(name = "expire_period")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expirePeriod;

    @Column(name = "payment_id")
    private Long paymentId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
