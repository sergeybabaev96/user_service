package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final static int COUNT_MONTHS = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        LocalDate monthsAgo = LocalDate.now().minusMonths(COUNT_MONTHS);

        User receiver = userRepository.getReferenceById(recommendationDto.getReceiverId());

        boolean recentRecommendationExists = receiver.getRecommendationsReceived().stream()
                .filter(recommendationReceived ->
                        recommendationReceived.getAuthor().getId().equals(recommendationDto.getAuthorId()))
                .noneMatch(recommendationReceived ->
                        recommendationReceived.getCreatedAt().isAfter(monthsAgo.atStartOfDay()));

        boolean allSkillsExist = recommendationDto.getSkillOffers().stream()
                .map(SkillOfferDto::getSkill)
                .allMatch(skill -> skillRepository.existsById(skill.getId()));

        if (!allSkillsExist) {
            throw new DataValidationException("Навык(и), предлагаемый(ые) в рекомендации не существует(ют)");
        } else if (recentRecommendationExists) {
            throw new DataValidationException("Автор дает рекомендацию раньше," +
                    "чем через 6 месяцев после его последней рекомендации этому пользователю.");
        }
        Recommendation recommendation = saveRecommendation(recommendationDto);

        List<Skill> skillOffers = recommendationDto.getSkillOffers().stream()
                .map(SkillOfferDto::getSkill)
                .toList();

        List<Skill> userSkills = skillRepository.findAllByUserId(recommendationDto.getReceiverId());
        Map<Boolean, List<Skill>> partitionedSkills = skillOffers.stream()
                .collect(Collectors.partitioningBy(userSkills::contains));

        partitionedSkills.get(false)
                .stream().filter(Objects::nonNull)
                .forEach(skill -> skillOfferRepository.create(skill.getId(),
                        recommendation.getId()));

        partitionedSkills.get(true)
                .forEach(skill -> {
                    if (!hasGuarantee(skill, recommendation.getAuthor(), recommendation.getReceiver())) {
                        addGuarantee(skill, recommendation.getAuthor(), recommendation.getReceiver());
                    }
                });

        return recommendationMapper.toDto(recommendation);
    }

    private Recommendation saveRecommendation(RecommendationDto recommendationDto) {
        Long newIdRecommendation = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()
        );
        return recommendationRepository.findById(newIdRecommendation)
                .orElseThrow(() -> new RuntimeException("Не удалось найти созданную рекомендацию"));
    }

    private boolean hasGuarantee(Skill skill, User guarantor, User receiver) {
        List<UserSkillGuarantee> guarantees = skill.getGuarantees();
        return guarantees.stream()
                .anyMatch(userSkillGuarantee ->
                        userSkillGuarantee.getUser().equals(receiver) &&
                        userSkillGuarantee.getGuarantor().equals(guarantor));
    }

    private void addGuarantee(Skill skill, User guarantor, User receiver) {
        userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                .user(receiver)
                .skill(skill)
                .guarantor(guarantor)
                .build());
    }
}
