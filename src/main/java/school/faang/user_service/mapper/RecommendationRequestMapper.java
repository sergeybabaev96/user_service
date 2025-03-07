package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    RecommendationRequest toRecommendationRequest(RecommendationRequestDto recommendationRequestDto);


    @Mapping(target = "skillsId", expression = "java(mapSkillsToIds(recommendationRequest.getSkills()))")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationRequestDto toRecommendationRequestDto(RecommendationRequest recommendationRequest);

    default List<Long> mapSkillsToIds(List<SkillRequest> skills) {
        return skills != null ? skills.stream()
                .map(SkillRequest::getId)
                .collect(Collectors.toList())
                : Collections.emptyList();
    }
}
