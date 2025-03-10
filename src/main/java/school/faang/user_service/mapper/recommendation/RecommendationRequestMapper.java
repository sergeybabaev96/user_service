package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.service.recommendation.SkillMapperService;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SkillMapperService.class})
public interface RecommendationRequestMapper {
    @Mapping(target = "skillsIds", source = "skills", qualifiedByName = "mapSkillsToIds")
    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "skills", source = "skillsIds")
    @Mapping(target = "requester.id", source = "requesterId")
    @Mapping(target = "receiver.id", source = "receiverId")
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Named("mapSkillsToIds")
    static List<Long> mapSkillsToIds(List<SkillRequest> skillRequests) {
        if (skillRequests == null) {
            return List.of();
        }
        return skillRequests.stream().map(SkillRequest::getId).toList();
    }
}
