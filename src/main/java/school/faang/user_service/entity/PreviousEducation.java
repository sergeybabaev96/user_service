package school.faang.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "previous_education")
public class PreviousEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "degree", nullable = false)
    private String degree; // пример: High School Diploma, Bachelor, etc.

    @Column(name = "institution", nullable = false)
    private String institution; // название учебного заведения

    @Column(name = "completion_year", nullable = false)
    private Integer completionYear; // год окончания

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // связь с пользователем
}
