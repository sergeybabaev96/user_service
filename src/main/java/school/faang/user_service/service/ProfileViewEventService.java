package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.ProfileViewEventDto;
import school.faang.user_service.publisher.ProfileViewEventPublisher;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProfileViewEventService {

    private final ProfileViewEventPublisher profileViewEventPublisher;

    public void publishProfileViewEvent(long viewerId, long profileOwnerId) {
        ProfileViewEventDto profileViewEventDto = ProfileViewEventDto.builder()
                .viewerId(viewerId)
                .profileOwnerId(profileOwnerId)
                .viewedAt(LocalDateTime.now())
                .build();
        profileViewEventPublisher.publish(profileViewEventDto);
        log.info("Event sent : {}", profileViewEventDto);
    }
}
