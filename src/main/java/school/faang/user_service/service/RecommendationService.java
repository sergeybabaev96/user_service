package school.faang.user_service.service;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.skilloffer.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;

    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillRepository skillRepository;

    /**
     * Создает новую рекомендацию
     * @param recommendation  объект DTO с данными о рекомендации
     * @return RecommendationDto - созданная рекомендация
     */
    public RecommendationDto create(@NonNull RecommendationDto recommendation) {
        validateRecommendation(recommendation);

        long receiverId = recommendation.getReceiverId();
        User user = getUser(receiverId);

        long authorId = recommendation.getAuthorId();
        User author = getUser(authorId);

        Recommendation recommendationEntity = recommendationMapper.toEntity(recommendation);
        recommendationEntity.setReceiver(user);
        recommendationEntity.setAuthor(author);
        recommendationEntity.setSkillOffers(getSkillOffers(recommendation));
        recommendationRepository.save(recommendationEntity);
        saveSkillsOffer(recommendation);

        return recommendationMapper.toDto(recommendationEntity);
    }

    /**
     * Обновляет существующую рекомендацию
     * @param recommendation - объект DTO с обновленными данными
     * @return RecommendationDto - обновленная рекомендация
     */
    public RecommendationDto update(@NonNull RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        long receiverId = recommendation.getReceiverId();
        long authorId = recommendation.getAuthorId();
        String content = recommendation.getContent();
        recommendationRepository.update(authorId, receiverId, content);

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        saveSkillsOffer(recommendation);

        Recommendation recommendationEntity = recommendationRepository.findById(recommendation.getId()).
                orElseThrow(() -> {
                    log.error("Рекомендация с ID {} не найдена", recommendation.getId());
                    return new DataValidationException("Recommendation not found");

                });
        return recommendationMapper.toDto(recommendationEntity);
    }

    /**
     * Удаляет рекомендацию по ее идентификатору
     * @param recommendationId - идентификатор рекомендации
     */
    public void delete(long recommendationId) {
        recommendationRepository.findById(recommendationId).orElseThrow(() -> {
            log.error("Рекомендация с ID {} не найдена", recommendationId);
            return new DataValidationException(String.format("Recommendation id %d not found", recommendationId));
        });

        recommendationRepository.deleteById(recommendationId);
    }

    /**
     * Получает список всех рекомендаций, полученных пользователем
     * @param receiverId - идентификатор пользователя
     * @return List<RecommendationDto> - список полученных рекомендаций
     */
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<Recommendation> recommendationPage =
                recommendationRepository.findAllByReceiverId(receiverId, pageable);
        return recommendationPage.getContent().stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    /**
     * Получает список всех рекомендаций, созданных пользователем
     * @param authorId - идентификатор автора
     * @return List<RecommendationDto> - список данных о рекомендациях
     */
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByAuthorId(authorId, pageable);
        return recommendationPage.getContent().stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    /**
     * Проверяет, что автор дает рекомендацию не раньше,
     * чем через 6 месяцев после его последней рекомендации этому пользователю.
     * Также проверяет, что предлагаемые в рекомендации навыки существуют в системе.
     * @param recommendation - объект DTO с рекомендацией
     */
    void validateRecommendation(RecommendationDto recommendation) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        LocalDateTime recommendationDate = recommendation.getUpdatedAt();

        if (recommendationDate.isAfter(sixMonthsAgo)) {
            log.error("Ошибка валидации: рекомендация обновлена слишком рано");
            throw new DataValidationException("Updated recommendation too early");
        }

        if (recommendation.getSkillOffers() == null || recommendation.getSkillOffers().isEmpty()) {
            log.error("Ошибка валидации: отсутствуют навыки в рекомендации");
            throw new DataValidationException("Skill offer is not found");
        }

        List<SkillOfferDto> skillOffers = recommendation.getSkillOffers();
        List<Long> skillIds = skillOffers.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
        for (Long skillId : skillIds) {
            skillOfferRepository.findById(skillId).orElseThrow(() -> {
                log.error("Навык с ID {} не найден в системе", skillId);
                return new DataValidationException("Skill offer not found");
            });
        }
    }

    /**
     * Сохранить предложенные в рекомендации скиллы в репозиторий SKillOfferRepository используя его метод create.
     * Если у пользователя, которому дают рекомендацию, такой скилл уже есть,
     * то добавить автора рекомендации гарантом к скиллу, который он предлагает,
     * если этот автор еще не стоит там гарантом.
     * @param recommendation  рекомендации
     */
    private void saveSkillsOffer(RecommendationDto recommendation) {
        long receiverId = recommendation.getReceiverId();
        long authorId = recommendation.getAuthorId();

        User receiver = getUser(receiverId);
        User author = getUser(authorId);

        for (SkillOfferDto skillOfferDto : recommendation.getSkillOffers()) {
            long skillId = skillOfferDto.getSkillId();
            skillOfferRepository.create(skillId, recommendation.getId());

            Skill skill = skillRepository.findById(skillId).orElseThrow(() -> {
                log.error("Ошибка: навык с ID {} не найден", skillId);
                return new DataValidationException("Skill not found");
            });

            if (receiver.getSkills().contains(skill)) {
                List<UserSkillGuarantee> userSkillGuarantees = skill.getGuarantees();
                List<User> guarantees = userSkillGuarantees.stream()
                        .map(UserSkillGuarantee::getGuarantor)
                        .toList();

                if (!guarantees.contains(author)) {
                    UserSkillGuarantee guarantee = new UserSkillGuarantee();
                    guarantee.setGuarantor(author);
                    guarantee.setSkill(skill);
                    guarantee.setUser(receiver);
                    userSkillGuarantees.add(guarantee);

                    skill.setGuarantees(userSkillGuarantees);
                    skillRepository.save(skill);
                    log.info("Добавлен гарант {} к навыку {}", authorId, skillId);

                }
            }
        }
    }

    /**
     * Получает пользователя по его идентификатору
     * @param userId - идентификатор пользователя
     * @return User - найденный пользователь
     */
    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: пользователь с ID {} не найден", userId);
            return new DataValidationException("User is not found");
        });
    }

    /**
     * Получает список предложенных навыков в рекомендации
     * @param recommendation - объект DTO с рекомендацией
     * @return List<SkillOffer> - список предложенных навыков
     */
    private List<SkillOffer> getSkillOffers(RecommendationDto recommendation) {
        List<SkillOffer> allSkillOffer = new ArrayList<>();
        for (SkillOfferDto skillOfferDto : recommendation.getSkillOffers()) {
            SkillOffer skillOffer = skillOfferRepository.findById(skillOfferDto.getId()).orElseThrow(() -> {
                log.error("Ошибка: навык с ID {} не найден", skillOfferDto.getId());
                return new DataValidationException("Skill not found");
            });
            allSkillOffer.add(skillOffer);

        }
        return allSkillOffer;
    }
}