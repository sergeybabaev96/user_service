package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface RecommendationRequestMapper {

    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto dto);

    RecommendationRequestDto toDto(RecommendationRequest entity);
}