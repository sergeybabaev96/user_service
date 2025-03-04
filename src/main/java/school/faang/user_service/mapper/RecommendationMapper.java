package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    @Mapping(target = "author", source = "authorId", qualifiedByName = "mapAuthorIdToUser")
    @Mapping(target = "receiver", source = "receiverId", qualifiedByName = "mapReceiverIdToUser")
    @Mapping(target = "skillOffers", source = "skillOfferIds")
    Recommendation toEntity(RecommendationDto recommendation);

    @Mapping(target = "skillOfferIds", source = "skillOffers")
    RecommendationDto toDto(Recommendation recommendation);

    default List<SkillOffer> mapSkillOfferIdsToSkillOffers(List<Long> skillOffersIds) {
        return skillOffersIds.stream()
                .map(id -> {
                    SkillOffer skillOffer = new SkillOffer();
                    skillOffer.setId(id);
                    return skillOffer;
                })
                .collect(Collectors.toList());
    }

    default List<Long> mapSkillOffersToSkillOfferIds(List<SkillOffer> skillOffers) {
        return skillOffers.stream()
                .map(SkillOffer::getId)
                .collect(Collectors.toList());
    }

    @Named("mapAuthorIdToUser")
    default User mapAuthorIdToUser(Long authorId) {
        if (authorId == null) {
            return null;
        }
        User user = new User();
        user.setId(authorId);
        return user;
    }

    @Named("mapReceiverIdToUser")
    default User mapReceiverIdToUser(Long receiverId) {
        if (receiverId == null) {
            return null;
        }
        User user = new User();
        user.setId(receiverId);
        return user;
    }
}
