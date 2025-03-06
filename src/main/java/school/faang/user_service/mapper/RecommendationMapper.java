package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(source = "skillOffers", target = "skillOffers")
    Recommendation toEntity(RecommendationDto recommendation);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers")
    RecommendationDto toDto(Recommendation recommendation);
}
