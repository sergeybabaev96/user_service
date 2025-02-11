package school.faang.user_service.mapper.Recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Component
@Mapper(componentModel = "spring", uses = SkillOfferMapper.class)
public interface RecommendationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers")
    @Mapping(target = "skillId", ignore = true)
    RecommendationDto toDto(Recommendation recommendation);
}