package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.recommendation.RecommendationRequestDto;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto dto);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skills", ignore = true)
    RecommendationRequestDto toDto(RecommendationRequest entity);
}
