package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.RecommendationEvent;
import school.faang.user_service.dto.recommendation.RecommendationDto;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecommendationEventMapper {

    @Mapping(target = "recommendationId", source = "id")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "receiverId", source = "receiverId")
    @Mapping(target = "createdAt", source = "createdAt", defaultExpression = "java(java.time.LocalDateTime.now())")
    RecommendationEvent mapToRecommendationEvent(RecommendationDto recommendation);
}