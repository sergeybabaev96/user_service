package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorshipRepository extends CrudRepository<Mentorship, Long> {
    boolean existsByMentorIdAndMenteeId(Long mentorId, Long menteeId);

    @Query("SELECT m.id FROM Mentorship m WHERE m.mentor.id = :mentorId AND m.mentee.id = :menteeId")
    Optional<Long> findIdByMentorIdAndMenteeId(long mentorId, long menteeId);

    @Query("SELECT m.mentee FROM Mentorship m WHERE m.mentor.id = :mentorId")
    List<User> findAllMenteesByMentorId(long mentorId);

    @Query("SELECT m.mentor FROM Mentorship m WHERE m.mentee.id = :menteeId")
    List<User> findAllMentorsByMenteeId(long menteeId);

//    TODO: метод не соответствует условию в задаче BJS2-66001: После деактивации профиля: если пользователь был
//     ментором других пользователей — необходимо остановить менторство. Для этого нужно написать соответствующий метод
//     в MentorshipService. Ментор должен пропасть из списка менторов своих менти. При этом все цели, которые ментор
//     поставил менти сохраняются, но больше не хранят mentorId — теперь они выглядят так,
//     как будто менти поставил их себе сам. Про ЦЕЛИ речь в условии, а не про связи в mentorship. Метод ниже
//     устанавливает менти самого себе ментором.
//    @Modifying
//    @Query(value = "UPDATE mentorship m SET m.mentor_id = m.mentee_id WHERE m.mentor_id = :mentorId",
//            nativeQuery = true)
//    void deactivateMentor(@Param("mentorId") Long mentorId);

    // TODO: удаление в задаче BJS2-66001 делать не просили
//    @Modifying
//    @Query(value = "DELETE mentorship m WHERE m.mentee_id = :menteeId",
//            nativeQuery = true)
//    void deleteDeactivatedMentee(@Param("menteeId") Long menteeId);
}