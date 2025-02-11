package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.Recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillValidator;
import school.faang.user_service.validator.UserValidator;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final SkillValidator skillValidator;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendation) {
        LocalDateTime recommendationThresholdDate = LocalDateTime.now().plusMonths(6);
        skillValidator.validatorSkillId(recommendation.getSkillId());
        userValidator.validatorUserExistence(recommendation.getReceiverId());
        recommendationValidator.validatorExistenceUserSkillGuarantee(recommendation);
        Optional<Recommendation> optionalRecommendation = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthorId(), recommendation.getReceiverId());
        optionalRecommendation.ifPresent(options
                -> recommendationValidator.validateRecommendationDate(options.getCreatedAt(), recommendationThresholdDate));

        if (skillRepository.findUserSkill(recommendation.getSkillId(), recommendation.getReceiverId()).isPresent()) {
            saveSkillGuarantees(recommendation);
            throw new DataValidationException("Рекомендация невозможна: у пользователя уже есть этот навык!");
        }
        long newRecommendationId = saveCreateRecommendation(recommendation);
        skillOfferRepository.create(recommendation.getSkillId(), newRecommendationId);

        return recommendationMapper.toDto(recommendationRepository.findById(newRecommendationId)
                .orElseThrow(() -> new IllegalStateException("Ошибка сохранения рекомендации!")));
    }

    private void saveSkillGuarantees(RecommendationDto recommendationDto) {
        User user = userRepository.getReferenceById(recommendationDto.getReceiverId());
        User guarantor = userRepository.getReferenceById(recommendationDto.getAuthorId());
        Skill skill = skillRepository.getReferenceById(recommendationDto.getSkillId());

        UserSkillGuarantee guarantees = UserSkillGuarantee.builder()
                .user(user)
                .skill(skill)
                .guarantor(guarantor)
                .build();

        userSkillGuaranteeRepository.save(guarantees);
    }
    private long saveCreateRecommendation(RecommendationDto recommendation){
        return recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
    }
}
