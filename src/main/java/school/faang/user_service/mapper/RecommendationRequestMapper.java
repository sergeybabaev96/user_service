package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.recommendation.request.RecommendationRequestDto;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "requester.id", source = "requesterId")
    @Mapping(target = "receiver.id", source = "receiverId")
    @Mapping(target = "recommendation", ignore = true)
    @Mapping(target = "id", ignore = true)
    school.faang.user_service.entity.recommendation.RecommendationRequest toEntity(RecommendationRequestDto dto);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skills", expression = "java(getSkillIds(request))")
    RecommendationRequestDto toDto(school.faang.user_service.entity.recommendation.RecommendationRequest request);

    default List<Long> getSkillIds(school.faang.user_service.entity.recommendation.RecommendationRequest request) {
        if (request == null) {
            return List.of();
        }
        return request.getSkills().stream().map(skillRequest -> skillRequest.getSkill().getId()).toList();
    }
}
