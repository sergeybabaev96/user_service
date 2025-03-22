package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {
    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto dto);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skills", ignore = true)
    RecommendationRequestDto toDto(RecommendationRequest entity);

    @Mapping(target = "skills", ignore = true)
    List<RecommendationRequest> toEntities(List<RecommendationRequestDto> dto);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skills", ignore = true)
    List<RecommendationRequestDto> toDtos(List<RecommendationRequest> entity);
}
