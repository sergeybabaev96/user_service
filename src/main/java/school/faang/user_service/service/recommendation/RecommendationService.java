package school.faang.user_service.service.recommendation;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
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
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skilloffer.SkillOfferService;
import school.faang.user_service.validation.recommendation.RecommendationValidator;

import java.util.List;
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
 *     <li>{@link #create(RecommendationCreateDto, long) Создание новой рекомендации} с проверкой валидности данных.</li>
 *     <li>{@link #update(RecommendationCreateDto, long) Обновление существующей рекомендации}.</li>
 *     <li>{@link #delete(long) Удаление рекомендации} по её идентификатору.</li>
 *     <li>{@link #getAllUserRecommendations(long,Pageable) Получение списка всех рекомендаций}, полученных пользователем.</li>
 *     <li>{@link #getAllCreatedRecommendation(long, Pageable) Получение списка всех рекомендаций}, созданных пользователем.</li>
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
 * @see RecommendationValidator
 */
@Slf4j
@Data
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationValidator recommendationValidator;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferMapper skillOfferMapper;
    private final SkillRepository skillRepository;
    private final SkillOfferService skillOfferService;

    /**
     * Создает новую рекомендацию
     * @param recommendation  объект DTO с данными о рекомендации
     * @param recommendationId айди рекомендации
     * @return RecommendationDto - созданная рекомендация
     */
    public RecommendationViewDto create(@NonNull RecommendationCreateDto recommendation,
                                        long recommendationId) {
        recommendationValidator.validate(recommendation);

        Recommendation recommendationEntity = getRecommendationEntity(recommendation);
        recommendationRepository.save(recommendationEntity);
        skillOfferService.saveSkillsOffer(recommendation, recommendationId);

        return recommendationMapper.toViewDto(recommendationEntity);
    }

    /**
     * Обновляет существующую рекомендацию
     * @param recommendation - объект DTO с обновленными данными
     * @param recommendationId айди рекомендации
     * @return RecommendationDto - обновленная рекомендация
     */
    public RecommendationViewDto update(@NonNull RecommendationCreateDto recommendation,
                                        long recommendationId) {
        recommendationValidator.validate(recommendation);

        long receiverId = recommendation.getReceiverId();
        long authorId = recommendation.getAuthorId();
        String content = recommendation.getContent();
        recommendationRepository.update(authorId, receiverId, content);

        skillOfferRepository.deleteAllByRecommendationId(recommendationId);

        skillOfferService.saveSkillsOffer(recommendation, recommendationId);
        Recommendation recommendationEntity = recommendationRepository.findById(recommendationId).
                orElseThrow(() -> {
                    log.error("Рекомендация с ID {} не найдена", recommendationId);
                    return new EntityNotFoundException("Recommendation not found");

                });
        return recommendationMapper.toViewDto(recommendationEntity);
    }

    /**
     * Удаляет рекомендацию по ее идентификатору
     * @param recommendationId - идентификатор рекомендации
     * @throws DataValidationException если рекомендация не найдена
     */
    public void delete(long recommendationId) {
        if (!recommendationRepository.existsById(recommendationId)) {
            log.error("Рекомендация с ID {} не найдена", recommendationId);
            throw new EntityNotFoundException(String.format("Recommendation id %d not found", recommendationId));
        }
        recommendationRepository.deleteById(recommendationId);
    }

    /**
     * Получает список всех рекомендаций, полученных пользователем
     * @param receiverId - идентификатор пользователя
     * @return List<RecommendationDto> - список полученных рекомендаций
     */
    public Page<RecommendationViewDto> getAllUserRecommendations(long receiverId,@NonNull Pageable pageable) {
        Page<Recommendation> recommendationPage =
                recommendationRepository.findAllByReceiverId(receiverId, pageable);
        return recommendationPage.map(recommendationMapper::toViewDto);
    }

    /**
     * Получает список всех рекомендаций, созданных пользователем
     * @param authorId - идентификатор автора
     * @return List<RecommendationDto> - список данных о рекомендациях
     */
    public Page<RecommendationViewDto> getAllCreatedRecommendation(long authorId, @NonNull Pageable pageable) {
        Page<Recommendation> recommendationPage = recommendationRepository
                .findAllByAuthorId(authorId, pageable);
        return recommendationPage.map(recommendationMapper::toViewDto);
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
            return new EntityNotFoundException("User is not found");
        });
    }

    /**
     * Маппит те поля ДТО рекомендации, которые были проигнорированы
     * @param recommendation ДТО рекомендации
     * @return Сущьность рекомендации
     */
    private Recommendation getRecommendationEntity(@NotNull RecommendationCreateDto recommendation) {
        long receiverId = recommendation.getReceiverId();
        User receiver = getUser(receiverId);

        long authorId = recommendation.getAuthorId();
        User author = getUser(authorId);

        Recommendation recommendationEntity = recommendationMapper.createDtoToEntity(recommendation);
        recommendationEntity.setReceiver(receiver);
        recommendationEntity.setAuthor(author);
        List<SkillOffer> skillOffers = skillOffersToEntity(recommendation);
        recommendationEntity.setSkillOffers(skillOffers);
        return recommendationEntity;
    }

    /**
     * Получает список предложенных навыков в рекомендации
     * @param recommendation - объект DTO с рекомендацией
     * @return List<SkillOffer> - список предложенных навыков
     * @throws DataValidationException если навык не найден
     */
    private List<SkillOffer> skillOffersToEntity(RecommendationCreateDto recommendation) {
        List<Long> skillOfferIds = recommendation.getSkillOffers().stream()
                .map(SkillOfferCreateDto::getSkillId)
                .toList();
        Iterable<SkillOffer> allSkillOffers = skillOfferRepository.findAllById(skillOfferIds);
        return StreamSupport.stream(allSkillOffers.spliterator(), false).toList();
    }
}