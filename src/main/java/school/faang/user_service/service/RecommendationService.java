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

        // This method does not return id of created entity!
        recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());

        if (recommendation.getSkillOffers() != null) {
            var receiverSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());
            recommendation.getSkillOffers()
                    .forEach(dto -> createSkillOffer(recommendation, dto, receiverSkills));
        }

        var createdRecommendation = recommendationRepository.findByAuthorIdAndReceiverId(
                recommendation.getAuthorId(),
                recommendation.getReceiverId()
        );
        if (createdRecommendation.isEmpty()) {
            throw new DataValidationException(
                    "Recommendation is not created (author id: %d, receiver id: %d)".formatted(
                            recommendation.getAuthorId(),
                            recommendation.getReceiverId()));
        }

        var createdRecommendationDto = recommendationMapper.toDto(createdRecommendation.get());
        createdRecommendationDto.setSkillOffers(recommendation.getSkillOffers());

        return createdRecommendationDto;
    }

    public RecommendationDto update(@NotNull RecommendationDto recommendation) {
        validateRecommendation(recommendation);

        if (recommendation.getId() == null) {
            throw new DataValidationException("Recommendation id is required");
        }

        recommendationRepository.update(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());

        skillOfferService.deleteAllByRecommendationId(recommendation.getId());

        if (recommendation.getSkillOffers() != null) {
            var receiverSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());
            var createdSkillOffers = recommendation.getSkillOffers()
                    .stream()
                    .map(dto -> createSkillOffer(recommendation, dto, receiverSkills))
                    .toList();

            recommendation.setSkillOffers(createdSkillOffers);
        }

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

        var page = pageFetcher.apply(getInitialPageRequest());
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
        var pageRequest = PageRequest.of(pageIndex, pageSize);
        var page = pageFetcher.apply(pageRequest);
        recommendationDtos.addAll(page.map(this::toRecommendationDto).toList());
    }

    @NotNull
    private RecommendationDto toRecommendationDto(Recommendation recommendation) {
        var recommendationDto = recommendationMapper.toDto(recommendation);
        var skillOfferDTOs = skillOfferService.findAllByRecommendationId(recommendation.getId())
                .stream()
                .map(skillOfferMapper::toDto)
                .toList();
        recommendationDto.setSkillOffers(skillOfferDTOs);

        return recommendationDto;
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        var lastAuthorRecommendation = recommendationRepository.findLastRecommendationByAuthorId(
                recommendation.getAuthorId());

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
        if (recommendation.getSkillOffers() == null) {
            return;
        }

        var existedSkillsInRecommendation = recommendation.getSkillOffers()
                .stream()
                .filter(dto -> skillRepository.existsById(dto.skillId()))
                .toList();
        if (existedSkillsInRecommendation.size() != recommendation.getSkillOffers().stream().distinct().count()) {
            throw new DataValidationException(
                    "Skills %s are not registered".formatted(
                            String.join(
                                    ", ",
                                    recommendation.getSkillOffers()
                                            .stream()
                                            .map(SkillOfferDto::skillId)
                                            .filter(skillId -> existedSkillsInRecommendation.stream()
                                                    .filter(
                                                            existedSkill -> existedSkill.skillId() == skillId)
                                                    .findFirst()
                                                    .isEmpty())
                                            .map(Object::toString)
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
                && userSkillGuaranteeRepository.findByGuarantorId(recommendation.getAuthorId()).isEmpty()) {
            userSkillGuaranteeRepository.create(
                    recommendation.getReceiverId(),
                    skillOfferDto.skillId(),
                    recommendation.getAuthorId());
        }

        // This method does not return id of created entity!
        skillOfferService.create(skillOfferDto.skillId(), recommendation.getId());
        var skillOffer = skillOfferService.findBySkillIdAndRecommendationId(
                skillOfferDto.skillId(),
                recommendation.getId());
        if (skillOffer.isEmpty()) {
            throw new DataValidationException(
                    "Skill offer is not created (skill id: %d, recommendation id: %d)".formatted(
                            skillOfferDto.skillId(),
                            recommendation.getId()));
        }

        return skillOfferMapper.toDto(skillOffer.get());
    }

    private Pageable getInitialPageRequest() {
        return PageRequest.of(0, pageSize);
    }
}
