package school.faang.user_service.service.skilloffer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;

    public List<SkillOffer> getSkillOffers(List<SkillOfferDto> skillOfferDto, long receiverId) {

        return skillOfferDto.stream().map(dto -> skillOfferRepository
                .findAllOffersOfSkill(dto.skillId(), receiverId)).flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public void deleteSkillOffer (long recommendationId) {
        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
    }

    public void create (long skillId, long recommendationId) {
        skillOfferRepository.create(skillId, recommendationId);
    }
}
