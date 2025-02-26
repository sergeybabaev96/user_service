package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.analytic.RecommendationAnalyticDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.CreateRecommendationResponse;
import school.faang.user_service.dto.recommendation.GetAllRecommendationsResponse;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.dto.recommendation.UpdateRecommendationResponse;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationMapper recommendationMapper;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final KafkaTemplate<String, RecommendationAnalyticDto> kafkaTemplate;
    private final RecommendationValidator recommendationValidator;

    @Value("${recommendation.analytic.recommendation-topic}")
    private String recommendationTopic;

    @Transactional
    public CreateRecommendationResponse create(CreateRecommendationRequest createRequest) {
        Recommendation recommendation = recommendationMapper.fromCreateRequest(createRequest);
        recommendation.setAuthor(userRepository.getReferenceById(createRequest.getAuthorId()));
        recommendation.setReceiver(userRepository.getReferenceById(createRequest.getReceiverId()));

        recommendationValidator.validateRecommendation(recommendation);
        recommendationValidator.validateOfferedSkills(createRequest.getSkillIds());

        Long recommendationId = recommendationRepository.create(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent());

        recommendation.setId(recommendationId);

        saveSkillOffers(recommendation, createRequest.getSkillIds());
        RecommendationAnalyticDto recommendationAnalyticDto = new RecommendationAnalyticDto();
        recommendationAnalyticDto.setRecommendationId(recommendationId);
        recommendationAnalyticDto.setAuthorId(recommendation.getAuthor().getId());
        recommendationAnalyticDto.setReceivedId(recommendation.getReceiver().getId());
        recommendationAnalyticDto.setReceivedAt(LocalDateTime.now());
        kafkaTemplate.send(recommendationTopic, recommendationAnalyticDto);

        return recommendationMapper.toCreateResponse(recommendation);
    }

    @Transactional
    public UpdateRecommendationResponse update(UpdateRecommendationRequest updateRequest) {
        Recommendation recommendation = recommendationMapper.fromUpdateRequest(updateRequest);
        recommendation.setAuthor(userRepository.getReferenceById(updateRequest.getAuthorId()));
        recommendation.setReceiver(userRepository.getReferenceById(updateRequest.getReceiverId()));

        recommendationValidator.validateRecommendation(recommendation);
        recommendationValidator.validateOfferedSkills(updateRequest.getSkillIds());

        recommendationRepository.update(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent());

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        saveSkillOffers(recommendation, updateRequest.getSkillIds());

        return recommendationMapper.toUpdateResponse(recommendation);
    }

    @Transactional
    public void delete(long id) {
        skillOfferRepository.deleteAllByRecommendationId(id);
        recommendationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<GetAllRecommendationsResponse> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendationPage = recommendationRepository
                .findAllByReceiverId(receiverId, Pageable.unpaged());

        return recommendationPage.get()
                .map(recommendationMapper::toGetAllResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetAllRecommendationsResponse> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> recommendationPage = recommendationRepository
                .findAllByAuthorId(authorId, Pageable.unpaged());

        return recommendationPage.get()
                .map(recommendationMapper::toGetAllResponse)
                .toList();
    }

    private void saveSkillOffers(Recommendation recommendation, List<Long> skillIds) {
        recommendation.setSkillOffers(new ArrayList<>());

        skillRepository.findAllById(skillIds)
                .forEach(skill -> {
                    Long skillOfferId = skillOfferRepository.create(skill.getId(), recommendation.getId());
                    recommendation.addSkillOffer(skillOfferRepository.findById(skillOfferId).orElseThrow());
                    addGuarantee(recommendation, skill);
                });
    }

    private void addGuarantee(Recommendation recommendation, Skill skill) {
        UserSkillGuarantee guarantee = userSkillGuaranteeRepository
                .findByUserIdAndSkillId(recommendation.getReceiver().getId(), skill.getId());

        if (guarantee == null) {
            UserSkillGuarantee newGuarantee = new UserSkillGuarantee(
                    null,
                    recommendation.getReceiver(),
                    skill,
                    recommendation.getAuthor());

            userSkillGuaranteeRepository.save(newGuarantee);
        } else {
            userSkillGuaranteeRepository.updateGuarantor(
                    recommendation.getReceiver().getId(),
                    skill.getId(),
                    recommendation.getAuthor().getId());
        }
    }
}
