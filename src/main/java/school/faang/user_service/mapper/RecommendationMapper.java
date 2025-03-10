package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.dto.recommendation.RecommendationDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillOffers", ignore = true)
    RecommendationDto toDto(Recommendation entity);

    @Mapping(target = "skillOffers", ignore = true)
    Recommendation toEntity(RecommendationDto dto);
}
