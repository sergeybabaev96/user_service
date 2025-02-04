package school.faang.user_service.data;

import school.faang.user_service.dto.recommendation.request.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.request.filter.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public enum RecommendationRequestData {
    DATA1(1L, UserData.USER1, UserData.USER1, "message", RequestStatus.ACCEPTED, "", null,
            List.of(SkillData.SKILL_DEV),
            LocalDateTime.now(),
            LocalDateTime.of(2024, 2, 2, 2, 2)),
    DATA_NULL_MESSAGE(1L, UserData.USER1, UserData.USER1, null, RequestStatus.ACCEPTED, "", null,
            List.of(SkillData.SKILL_DEV),
            LocalDateTime.now(),
            LocalDateTime.of(2024, 2, 2, 2, 2))
    ;

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final long id;
    private final UserData requester;
    private final UserData receiver;
    private final String message;
    private final RequestStatus status;
    private final String rejectionReason;
    private final RecommendationData recommendation;
    private final List<SkillData> skillsRequested;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    RecommendationRequestData(long id,
                              UserData requester,
                              UserData receiver,
                              String message,
                              RequestStatus status,
                              String rejectionReason,
                              RecommendationData recommendation,
                              List<SkillData> skillsRequested,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
        this.id = id;
        this.requester = requester;
        this.receiver = receiver;
        this.message = message;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.recommendation = recommendation;
        this.skillsRequested = skillsRequested;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public RecommendationRequestDto toDto() {
        return RecommendationRequestDto
                .builder()
                .id(this.id)
                .skills(this.skillsRequested.stream().map(SkillData::getId).toList())
                .requesterId(this.requester.getId())
                .receiverId(this.receiver.getId())
                .status(this.status.name())
                .message(this.message)
                .createdAt(this.createdAt.format(DATE_TIME_FORMAT))
                .updatedAt(this.updatedAt.format(DATE_TIME_FORMAT))
                .rejectionReason(this.rejectionReason)
                .build();
    }

    public school.faang.user_service.entity.recommendation.RecommendationRequest toRecommendationRequest() {
        return school.faang.user_service.entity.recommendation.RecommendationRequest.builder()
                .id(this.id)
                .skills(this.skillsRequested.stream()
                        .map(skillData -> SkillRequest.builder()
                                .skill(skillData.toSkill())
                                .build())
                        .toList())
                .requester(this.requester.getUser())
                .receiver(this.receiver.getUser())
                .message(this.message)
                .status(this.status)
                .rejectionReason(this.rejectionReason)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    public RecommendationRequestFilterDto toFilterDto() {
        return RecommendationRequestFilterDto.builder()
                .requesterId(requester.getId())
                .receiverId(receiver.getId())
                .message(message)
                .status(status.name())
                .skillIds(skillsRequested.stream().map(SkillData::getId).toList())
                .createdAt(createdAt.format(DATE_TIME_FORMAT))
                .updatedAt(updatedAt.format(DATE_TIME_FORMAT))
                .build();
    }

    public long getId() {
        return id;
    }

    public UserData getRequester() {
        return requester;
    }

    public String getMessage() {
        return message;
    }

    public List<SkillData> getSkillsRequested() {
        return skillsRequested;
    }
}
