package school.faang.user_service.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private static final int RECOMMENDATION_MIN_DISTANCE_MONTHS = 6;
    private static final int PAGE_SIZE = 100;
    private static final Pageable initialPageRequest = PageRequest.of(0, PAGE_SIZE);

    private final SkillOfferService skillOfferService;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    private final RecommendationMapper recommendationMapper;
    private final SkillOfferMapper skillOfferMapper;

    public RecommendationDto create(@NotNull RecommendationDto recommendation) {
        validateRecommendation(recommendation);

        var receiverSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());
        recommendation.getSkillOffers()
                .forEach(dto -> createSkillOffer(recommendation, dto, receiverSkills));

        var recommendationId = recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());

        var createdRecommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new DataRetrievalFailureException("Recommendation is not found"));

        recommendationMapper.update(recommendation, createdRecommendation);

        return recommendation;
    }

    public RecommendationDto update(@NotNull RecommendationDto recommendation) {
        validateRecommendation(recommendation);

        recommendationRepository.update(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());

        skillOfferService.deleteAllByRecommendationId(recommendation.getId());

        var receiverSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());
        var createdSkillOffers = recommendation.getSkillOffers()
                .stream()
                .map(dto -> createSkillOffer(recommendation, dto, receiverSkills))
                .toList();

        recommendation.setSkillOffers(createdSkillOffers);

        return recommendation;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return getRecommendationDtos(
                pageRequest -> recommendationRepository.findAllByReceiverId(receiverId, pageRequest));
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return getRecommendationDtos(
                pageRequest -> recommendationRepository.findAllByAuthorId(authorId, pageRequest));
    }

    private List<RecommendationDto> getRecommendationDtos(Function<Pageable, Page<Recommendation>> pageFetcher) {
        List<RecommendationDto> recommendationDtos = new ArrayList<>();

        var page = pageFetcher.apply(initialPageRequest);
        if (page.isEmpty()) {
            return recommendationDtos;
        }

        recommendationDtos.addAll(page.map(this::toRecommendationDto).toList());
        IntStream.range(1, page.getTotalPages())
                .forEach(pageIndex -> processRecommendationsPage(pageIndex, recommendationDtos, pageFetcher));

        return recommendationDtos;
    }

    private void processRecommendationsPage(
            int pageIndex,
            List<RecommendationDto> recommendationDtos,
            Function<Pageable, Page<Recommendation>> pageFetcher) {
        var pageRequest = PageRequest.of(pageIndex, PAGE_SIZE);
        var page = pageFetcher.apply(pageRequest);
        recommendationDtos.addAll(page.map(this::toRecommendationDto).toList());
    }

    @NotNull
    private RecommendationDto toRecommendationDto(Recommendation entity) {
        var dto = recommendationMapper.toDto(entity);
        var skillOfferDtos = entity.getSkillOffers()
                .stream()
                .map(skillOfferMapper::toDto)
                .toList();
        dto.setSkillOffers(skillOfferDtos);

        return dto;
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        var lastAuthorRecommendation = recommendationRepository.findLastRecommendationByAuthorId(
                recommendation.getAuthorId());

        if (lastAuthorRecommendation.isPresent()
                && LocalDateTime.now()
                .minusMonths(RECOMMENDATION_MIN_DISTANCE_MONTHS)
                .isAfter(lastAuthorRecommendation.get().getUpdatedAt())) {
            throw new DataValidationException(String.format(
                    "Less than %d months have passed since the last recommendation",
                    RECOMMENDATION_MIN_DISTANCE_MONTHS));
        }

        var existedSkillsInRecommendation = recommendation.getSkillOffers()
                .stream()
                .filter(dto -> skillRepository.findById(dto.getSkillId()).isPresent())
                .toList();
        if (existedSkillsInRecommendation.size() != recommendation.getSkillOffers().stream().distinct().count()) {
            throw new DataValidationException(String.format(
                    "Skills %s are not registered",
                    String.join(
                            ", ",
                            recommendation.getSkillOffers()
                                    .stream()
                                    .map(SkillOfferDto::getSkillId)
                                    .filter(skillId -> existedSkillsInRecommendation.stream()
                                            .filter(existedSkill -> existedSkill.getSkillId() == skillId)
                                            .findFirst()
                                            .isEmpty())
                                    .map(skillRepository::findById)
                                    .filter(Optional::isPresent)
                                    .map(x -> x.get().getTitle())
                                    .toList())));
        }
    }

    private SkillOfferDto createSkillOffer(
            RecommendationDto recommendation,
            SkillOfferDto skillOfferDto,
            List<Skill> receiverSkills) {
        var existedSkill = receiverSkills.stream()
                .filter(x -> x.getId() == skillOfferDto.getSkillId())
                .findFirst();
        if (existedSkill.isPresent()
                && userSkillGuaranteeRepository.findByGuarantorId(recommendation.getAuthorId()).isEmpty()) {
            userSkillGuaranteeRepository.create(
                    recommendation.getReceiverId(),
                    skillOfferDto.getSkillId(),
                    recommendation.getAuthorId());
        }

        var skillOfferId = skillOfferService.create(skillOfferDto.getSkillId(), recommendation.getId());
        var skillOffer = skillOfferService.findById(skillOfferId);

        return skillOfferMapper.toDto(skillOffer);
    }
}
