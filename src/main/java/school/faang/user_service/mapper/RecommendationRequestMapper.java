package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto dto);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(target = "skillIds", expression = "java(mapSkillsToIds(entity.getSkills()))")
    RecommendationRequestDto toDto(RecommendationRequest entity);

    default List<Long> mapSkillRequestsToIds(List<SkillRequest> skills) {
        if (skills == null) {
            return Collections.emptyList();
        }
        return skills.stream()
                .map(SkillRequest::getId)
                .collect(Collectors.toList());
    }
}
