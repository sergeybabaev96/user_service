package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.skilloffer.SkillOfferCreateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillOfferService {

    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public void saveSkillsOffer(@NonNull RecommendationCreateDto recommendation, long recommendationId) {
        if (recommendation.getSkillOffers() == null || recommendation.getSkillOffers().isEmpty()) {
            log.warn("Список skillOffers пуст или равен null для рекомендации с ID {}", recommendationId);
            throw new DataValidationException("skillOffers is not found or Empty");
        }

        long receiverId = recommendation.getReceiverId();
        long authorId = recommendation.getAuthorId();

        User receiver = getUser(receiverId);
        User author = getUser(authorId);

        for (SkillOfferCreateDto skillOfferDto : recommendation.getSkillOffers()) {
            long skillId = skillOfferDto.getSkillId();
            skillOfferRepository.create(skillId, recommendationId);

            Skill skill = skillRepository.findById(skillId).orElseThrow(() -> {
                log.error("Ошибка: навык с ID {} в skillRepository не найден", skillId);
                return new DataValidationException("Skill not found");
            });

            if (receiver.getSkills().contains(skill)) {
                List<UserSkillGuarantee> userSkillGuarantees = skill.getGuarantees();
                List<User> guarantors = userSkillGuarantees.stream()
                        .map(UserSkillGuarantee::getGuarantor)
                        .toList();

                if (!guarantors.contains(author)) {
                    UserSkillGuarantee guarantee = new UserSkillGuarantee();
                    guarantee.setGuarantor(author);
                    guarantee.setSkill(skill);
                    guarantee.setUser(receiver);
                    userSkillGuarantees.add(guarantee);

                    skill.setGuarantees(userSkillGuarantees);
                    skillRepository.save(skill);
                    log.info("Добавлен гарант {} к навыку {} для рекомендации {}"
                            , authorId, skillId, recommendationId);
                }

            }
        }
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: пользователь с ID {} не найден", userId);
            return new EntityNotFoundException("User is not found");
        });
    }
}
