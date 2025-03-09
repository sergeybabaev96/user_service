package school.faang.user_service.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.dto.recommendation.SkillOfferDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final SkillOfferService skillOfferService;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    private final RecommendationMapper recommendationMapper;
    private final SkillOfferMapper skillOfferMapper;

    @Value("${recommendationMinDistanceMonths}")
    private int recommendationMinDistanceMonths;

    @Value("${pageSize}")
    private int pageSize;

    public RecommendationDto create(@NotNull RecommendationDto recommendation) {
        validateRecommendation(recommendation);

        var receiverSkills = skillRepository.findAllByUserId(recommendation.receiverId());
        recommendation.skillOffers()
                .forEach(dto -> createSkillOffer(recommendation, dto, receiverSkills));

        var recommendationId = recommendationRepository.create(
                recommendation.authorId(),
                recommendation.receiverId(),
                recommendation.content());

        return new RecommendationDto(
                recommendationId,
                recommendation.authorId(),
                recommendation.receiverId(),
                recommendation.content(),
                recommendation.skillOffers(),
                recommendation.createdAt());
    }

    public RecommendationDto update(@NotNull RecommendationDto recommendation) {
        validateRecommendation(recommendation);

        recommendationRepository.update(
                recommendation.authorId(),
                recommendation.receiverId(),
                recommendation.content());

        skillOfferService.deleteAllByRecommendationId(recommendation.id());

        var receiverSkills = skillRepository.findAllByUserId(recommendation.receiverId());
        var createdSkillOffers = recommendation.skillOffers()
                .stream()
                .map(dto -> createSkillOffer(recommendation, dto, receiverSkills))
                .toList();

        recommendation.skillOffers().clear();
        recommendation.skillOffers().addAll(createdSkillOffers);

        return recommendation;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return getRecommendationDTOs(
                pageRequest -> recommendationRepository.findAllByReceiverId(receiverId, pageRequest));
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return getRecommendationDTOs(
                pageRequest -> recommendationRepository.findAllByAuthorId(authorId, pageRequest));
    }

    private List<RecommendationDto> getRecommendationDTOs(Function<Pageable, Page<Recommendation>> pageFetcher) {
        List<RecommendationDto> recommendationDTOs = new ArrayList<>();

        var page = pageFetcher.apply(getInitialPageRequest());
        if (page.isEmpty()) {
            return recommendationDTOs;
        }

        recommendationDTOs.addAll(page.map(this::toRecommendationDto).toList());
        IntStream.range(1, page.getTotalPages())
                .forEach(pageIndex -> processRecommendationsPage(pageIndex, recommendationDTOs, pageFetcher));

        return recommendationDTOs;
    }

    private void processRecommendationsPage(
            int pageIndex,
            List<RecommendationDto> recommendationDTOs,
            Function<Pageable, Page<Recommendation>> pageFetcher) {
        var pageRequest = PageRequest.of(pageIndex, pageSize);
        var page = pageFetcher.apply(pageRequest);
        recommendationDTOs.addAll(page.map(this::toRecommendationDto).toList());
    }

    @NotNull
    private RecommendationDto toRecommendationDto(Recommendation entity) {
        var recommendationDto = recommendationMapper.toDto(entity);
        var skillOfferDTOs = entity.getSkillOffers()
                .stream()
                .map(skillOfferMapper::toDto)
                .toList();
        recommendationDto.skillOffers().clear();
        recommendationDto.skillOffers().addAll(skillOfferDTOs);

        return recommendationDto;
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        var lastAuthorRecommendation = recommendationRepository.findLastRecommendationByAuthorId(
                recommendation.authorId());

        if (lastAuthorRecommendation.isPresent()
                && LocalDateTime.now()
                .minusMonths(recommendationMinDistanceMonths)
                .isBefore(lastAuthorRecommendation.get().getUpdatedAt())) {
            throw new DataValidationException(
                    "Less than %d months have passed since the last recommendation".formatted(
                            recommendationMinDistanceMonths));
        }

        validateSkillOffers(recommendation);
    }

    private void validateSkillOffers(RecommendationDto recommendation) {
        var existedSkillsInRecommendation = recommendation.skillOffers()
                .stream()
                .filter(dto -> skillRepository.existsById(dto.skillId()))
                .toList();
        if (existedSkillsInRecommendation.size() != recommendation.skillOffers().stream().distinct().count()) {
            throw new DataValidationException(
                    "Skills %s are not registered".formatted(
                            String.join(
                                    ", ",
                                    recommendation.skillOffers()
                                            .stream()
                                            .map(SkillOfferDto::skillId)
                                            .filter(skillId -> existedSkillsInRecommendation.stream()
                                                    .filter(
                                                            existedSkill -> existedSkill.skillId() == skillId)
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
                .filter(x -> x.getId() == skillOfferDto.skillId())
                .findFirst();
        if (existedSkill.isPresent()
                && userSkillGuaranteeRepository.findByGuarantorId(recommendation.authorId()).isEmpty()) {
            userSkillGuaranteeRepository.create(
                    recommendation.receiverId(),
                    skillOfferDto.skillId(),
                    recommendation.authorId());
        }

        var skillOfferId = skillOfferService.create(skillOfferDto.skillId(), recommendation.id());
        var skillOffer = skillOfferService.findById(skillOfferId);

        return skillOfferMapper.toDto(skillOffer);
    }

    private Pageable getInitialPageRequest() {
        return PageRequest.of(0, pageSize);
    }
}
