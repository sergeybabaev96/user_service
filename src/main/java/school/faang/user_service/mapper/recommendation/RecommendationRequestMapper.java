package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = SkillRequestMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RecommendationRequestMapper {
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "skills", target = "skillRequests")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(source = "skillRequests", target = "skills")
    @Mapping(target = "id", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequest);

    List<RecommendationRequestDto> toDtoList(List<RecommendationRequest> recommendationRequests);
}
