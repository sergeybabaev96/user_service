package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.utils.validationUtils.RecommendationValidation;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        RecommendationValidation.validateRecommendationContent(recommendationDto.getContent());
        RecommendationValidation.validateSkills(recommendationDto, skillOfferRepository.findAllSkillOffers());
        LocalDateTime lastRecommendation = recommendationRepository.
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId()).map(Recommendation::getCreatedAt).orElse(null);
        RecommendationValidation.validateRecommendationDate(lastRecommendation);

        Recommendation recommendation = recommendationMapper.toRecommendation(recommendationDto);
        recommendation.setCreatedAt(LocalDateTime.now());

        Long recommendationId = createRecommendation(recommendation);
        recommendation.setId(recommendationId);

        createSkillOffer(recommendation);
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        RecommendationValidation.validateRecommendationContent(recommendationDto.getContent());
        RecommendationValidation.validateSkills(recommendationDto, skillOfferRepository.findAllSkillOffers());
        LocalDateTime lastRecommendation = recommendationRepository.
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId()).map(Recommendation::getCreatedAt).orElse(null);
        RecommendationValidation.validateRecommendationDate(lastRecommendation);

        Recommendation recommendation = recommendationMapper.toRecommendation(recommendationDto);

        updateRecommendation(recommendation);
        deleteAllAndCreate(recommendation);
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new DataValidationException("Id не должен быть null");
        }
        recommendationRepository.deleteById(id);
        log.info("Рекомендация id: {} была удалена", id);
    }

    public List<RecommendationDto> getAllGivenRecommendation(Long authorId) {
        if (authorId == null) {
            throw new DataValidationException("Id автора не может быть null");
        }
        Pageable pageable = PageRequest.of(0 , 10);
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByAuthorId(authorId, pageable);
        List<Recommendation> recommendationList = recommendationPage.getContent();
        return recommendationMapper.toRecommendationDtoList(recommendationList);
    }

    public List<RecommendationDto> getAllUserRecommendations(Long receiverId) {
        if (receiverId == null) {
            throw new DataValidationException("Id пользователя не может быть null");
        }
        Pageable pageable = PageRequest.of(0 , 10);
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByReceiverId(receiverId, pageable);
        List<Recommendation> recommendationList = recommendationPage.getContent();
        return recommendationMapper.toRecommendationDtoList(recommendationList);
    }

    private void createSkillOffer(Recommendation recommendation) {
        List<SkillOffer> skillOfferListOfReceiver =
                skillOfferRepository.findAllByUserId(recommendation.getReceiver().getId());
        List<SkillOffer> skillOffersOfRecommendation = recommendation.getSkillOffers();
        for (SkillOffer skillOffer : skillOffersOfRecommendation) {
            skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId());
            if (skillOfferListOfReceiver.contains(skillOffer)) {
                UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
                userSkillGuarantee.setUser(recommendation.getReceiver());
                userSkillGuarantee.setSkill(skillOffer.getSkill());
                userSkillGuarantee.setGuarantor(recommendation.getAuthor());
                skillOffer.getSkill().setGuarantees(List.of(userSkillGuarantee));
                userSkillGuaranteeRepository.save(userSkillGuarantee);
            }
        }
    }

    private Long createRecommendation(Recommendation recommendation) {
        Long authorId = recommendation.getAuthor().getId();
        Long receiverId = recommendation.getReceiver().getId();
        if (authorId == null) {
            throw new DataValidationException("Id of author can't be null");
        } else if (receiverId == null) {
            throw new DataValidationException("Id of receiver can't be null");
        }
        String content = recommendation.getContent();
        return recommendationRepository.create(authorId, receiverId, content);
    }

    private void deleteAllAndCreate(Recommendation recommendation) {
        List<SkillOffer> skillOffersOfReceiver =
                skillOfferRepository.findAllByUserId(recommendation.getReceiver().getId());

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            if (skillOffer.getSkill() == null) {
                throw new DataValidationException("Скилл не может быть null");
            }
            skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId());
            if (skillOffersOfReceiver.contains(skillOffer)) {
                UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
                userSkillGuarantee.setUser(recommendation.getReceiver());
                userSkillGuarantee.setGuarantor(recommendation.getAuthor());
                userSkillGuarantee.setSkill(skillOffer.getSkill());
                skillOffer.getSkill().setGuarantees(List.of(userSkillGuarantee));
                userSkillGuaranteeRepository.save(userSkillGuarantee);
            }
        }
    }

    private void updateRecommendation(Recommendation recommendation) {
        Long authorId = recommendation.getAuthor().getId();
        Long receiverId = recommendation.getReceiver().getId();
        String content = recommendation.getContent();
        if (authorId == null) {
            throw new DataValidationException("Author id can't be null");
        } else if (receiverId == null) {
            throw new DataValidationException("Author id can't be null");
        }

        recommendationRepository.update(authorId, receiverId, content);
    }
}
