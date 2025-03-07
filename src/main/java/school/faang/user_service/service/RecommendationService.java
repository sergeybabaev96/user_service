package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        if (validateTime(recommendationDto) && validateSkillExist(recommendationDto)) {
            saveSkillOffers(recommendationDto);
            //todo:добавить гаранта, если у получателя уже есть этот скилл, если автор еще не гарант. Пока не понимаю, как это реализовать.
            recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        }
        return recommendationDto;
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        if (validateTime(recommendationDto) && validateSkillExist(recommendationDto)) {
            recommendationRepository.update(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
            recommendationDto.getSkillOffers().forEach(skillOfferDto -> {
                skillOfferRepository.deleteAllByRecommendationId(skillOfferDto.getRecommendationId());
            });
            recommendationDto.getSkillOffers().forEach(skillOfferDto -> {
                skillOfferRepository.create(skillOfferDto.getSkillId(), skillOfferDto.getRecommendationId());
            });
            //todo:  Если у пользователя-получателя рекомендации уже есть предлагаемый навык,
            // то автор рекомендации должен быть добавлен в список гарантов (skill.addGuarantee)
        }
        return recommendationDto;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        List<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(recieverId, Pageable.unpaged()).toList();
        return recommendations.stream().map(recommendationMapper::toDto).toList();
    }

    private boolean validateTime(RecommendationDto recommendationDto) {
        Optional<Recommendation> optional = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        if (optional.isPresent()) {
            Recommendation lastRecommendation = optional.get();
            return ChronoUnit.MONTHS
                    .between(lastRecommendation.getCreatedAt(), recommendationDto.getCreatedAt()) > 6;
        } else {
            throw new DataValidationException("6 months have not passed");
        }
    }

    private boolean validateSkillExist(RecommendationDto recommendationDto) {
        recommendationDto.getSkillOffers().forEach(skillOfferDto -> {
            if (!skillRepository.existsById(skillOfferDto.getSkillId())) {
                throw new DataValidationException("Skill doesn't exist in database");
            }
        });
        return true;
    }

    private void saveSkillOffers(RecommendationDto recommendationDto) {
        recommendationDto.getSkillOffers().forEach(skillOfferDto -> {
            skillOfferRepository.create(skillOfferDto.getSkillId(), skillOfferDto.getRecommendationId());
        });
    }

    public void addGuarantee(RecommendationDto recommendationDto) {
        //todo: как-то реализовать этот метод
    }
}
