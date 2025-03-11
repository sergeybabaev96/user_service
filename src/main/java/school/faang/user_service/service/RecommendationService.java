package school.faang.user_service.service;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationViewDto;
import school.faang.user_service.dto.skilloffer.SkillOfferCreateDto;
import school.faang.user_service.dto.skilloffer.SkillOfferViewDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Сервис для управления рекомендациями пользователей.
 * <p>
 * Этот сервис предоставляет методы для создания, обновления, удаления и получения рекомендаций,
 * а также для управления навыками, предлагаемыми в рекомендациях.
 * </p>
 * <p>
 * Основные функции:
 * <ul>
 *     <li>{@link #create(RecommendationCreateDto) Создание новой рекомендации} с проверкой валидности данных.</li>
 *     <li>{@link #update(RecommendationCreateDto) Обновление существующей рекомендации}.</li>
 *     <li>{@link #delete(long) Удаление рекомендации} по её идентификатору.</li>
 *     <li>{@link #getAllUserRecommendations(long) Получение списка всех рекомендаций}, полученных пользователем.</li>
 *     <li>{@link #getAllGivenRecommendations(long) Получение списка всех рекомендаций}, созданных пользователем.</li>
 *     <li>{@link #validateRecommendation(RecommendationCreateDto) Проверка валидности рекомендации}, включая проверку временных интервалов и существования навыков.</li>
 *     <li>{@link #saveSkillsOffer(RecommendationCreateDto) Сохранение предложенных навыков} в рекомендации и добавление гарантов к навыкам пользователя.</li>
 * </ul>
 * </p>
 * @author marsel_mkh
 * @see RecommendationViewDto
 * @see RecommendationCreateDto
 * @see SkillOfferViewDto
 * @see Recommendation
 * @see SkillOffer
 * @see User
 * @see Skill
 * @see UserSkillGuarantee
 */
@Slf4j
@Data
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;

    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferMapper skillOfferMapper;
    private final SkillRepository skillRepository;

    /**
     * Создает новую рекомендацию
     * @param recommendation  объект DTO с данными о рекомендации
     * @return RecommendationDto - созданная рекомендация
     */
    public RecommendationViewDto create(@NonNull RecommendationCreateDto recommendation) {
        validateRecommendation(recommendation);

        long receiverId = recommendation.getReceiverId();
        User receiver = getUser(receiverId);

        long authorId = recommendation.getAuthorId();
        User author = getUser(authorId);
        Recommendation recommendationEntity = recommendationMapper.CreateDtoToEntity(recommendation);
        recommendationEntity.setReceiver(receiver);
        recommendationEntity.setAuthor(author);
        List<SkillOffer> skillOffers = SkillOffersToEntity(recommendation);
        recommendationEntity.setSkillOffers(skillOffers);
        recommendationRepository.save(recommendationEntity);
        saveSkillsOffer(recommendation);

        return recommendationMapper.toDto(recommendationEntity);
    }

    /**
     * Обновляет существующую рекомендацию
     * @param recommendation - объект DTO с обновленными данными
     * @return RecommendationDto - обновленная рекомендация
     */
    public RecommendationViewDto update(@NonNull RecommendationCreateDto recommendation) {
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
     * @throws DataValidationException если рекомендация не найдена
     */
    public void delete(long recommendationId) {
        if (!recommendationRepository.existsById(recommendationId)) {
            log.error("Рекомендация с ID {} не найдена", recommendationId);
            throw new DataValidationException(String.format("Recommendation id %d not found", recommendationId));
        }
        recommendationRepository.deleteById(recommendationId);
    }

    /**
     * Получает список всех рекомендаций, полученных пользователем
     * @param receiverId - идентификатор пользователя
     * @return List<RecommendationDto> - список полученных рекомендаций
     */
    public List<RecommendationViewDto> getAllUserRecommendations(long receiverId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<Recommendation> recommendationPage =
                recommendationRepository.findAllByReceiverId(receiverId, pageable).toList();
        return recommendationPage.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    /**
     * Получает список всех рекомендаций, созданных пользователем
     * @param authorId - идентификатор автора
     * @return List<RecommendationDto> - список данных о рекомендациях
     */
    public List<RecommendationViewDto> getAllGivenRecommendations(long authorId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<Recommendation> recommendationPage = recommendationRepository
                .findAllByAuthorId(authorId, pageable).toList();
        return recommendationPage.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    /**
     * Проверяет, что автор дает рекомендацию не раньше,
     * чем через 6 месяцев после его последней рекомендации этому пользователю.
     * Также проверяет, что предлагаемые в рекомендации навыки существуют в системе.
     * @param recommendation - объект DTO с рекомендацией
     * @throws DataValidationException если рекомендация не прошла валидацию
     */
    private void validateRecommendation(RecommendationCreateDto recommendation) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        LocalDateTime recommendationUpdateAt = getUpdateAtCreateRecommendation(recommendation)
                .orElseThrow(() -> new DataValidationException("Update date is not found"));

        if (recommendationUpdateAt.isBefore(sixMonthsAgo)) {
            log.error("Ошибка валидации: рекомендация обновлена слишком рано");
            throw new DataValidationException("Updated recommendation too early");
        }

        if (recommendation.getSkillOffers() == null || recommendation.getSkillOffers().isEmpty()) {
            log.error("Ошибка валидации: отсутствуют навыки в рекомендации");
            throw new DataValidationException("Skill offer is not found");
        }

        List<SkillOfferCreateDto> skillOffers = recommendation.getSkillOffers();
        List<Long> skillIds = skillOffers.stream()
                .map(SkillOfferCreateDto::getSkillId)
                .toList();
        for (Long skillId : skillIds) {
            if (!skillRepository.existsById(skillId)) {
                log.error("Навык с ID {} не найден в системе", skillId);
                throw new DataValidationException("Skill offer not found");
            }
        }
    }
    private Optional<LocalDateTime> getUpdateAtCreateRecommendation(RecommendationCreateDto recommendation) {
        User recommendationAuthor  = getUser(recommendation.getAuthorId());
        return recommendationAuthor.getRecommendationsGiven().stream()
                .filter(rec -> {
                    User receiver = rec.getReceiver();
                    User createRecommendationReceiver = getUser(receiver.getId());
                    return receiver.equals(createRecommendationReceiver);
                })
                .findFirst()
                .map(Recommendation::getUpdatedAt);
    }

    /**
     * Сохраняет предложенные в рекомендации навыки в репозиторий {@link SkillOfferRepository} используя его метод {@code create}.
     * Если у пользователя, которому дают рекомендацию, такой скилл уже есть,
     * то добавляет автора рекомендации гарантом к скиллу, который он предлагает,
     * если этот автор еще не стоит там гарантом.
     * @param recommendation рекомендация
     * @throws DataValidationException если навык не найден
     */
    private void saveSkillsOffer(RecommendationCreateDto recommendation) {
        long receiverId = recommendation.getReceiverId();
        long authorId = recommendation.getAuthorId();

        User receiver = getUser(receiverId);
        User author = getUser(authorId);

        for (SkillOfferCreateDto skillOfferDto : recommendation.getSkillOffers()) {
            long skillId = skillOfferDto.getSkillId();
            skillOfferRepository.create(skillId, recommendation.getId());

            Skill skill = skillRepository.findById(skillId).orElseThrow(() -> {
                log.error("Ошибка: навык с ID {} в skillRepository не найден", skillId);
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
     * @throws DataValidationException если пользователь не найден
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
     * @throws DataValidationException если навык не найден
     */
    private List<SkillOffer> SkillOffersToEntity(RecommendationCreateDto recommendation) {
        List<Long> skillOfferIds = recommendation.getSkillOffers().stream()
                .map(SkillOfferCreateDto::getSkillId)
                .toList();
        Iterable<SkillOffer> allSkillOffers = skillOfferRepository.findAllById(skillOfferIds);
        return StreamSupport.stream(allSkillOffers.spliterator(), false).toList();
    }
}