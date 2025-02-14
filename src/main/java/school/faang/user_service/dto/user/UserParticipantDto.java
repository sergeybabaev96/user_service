package school.faang.user_service.dto.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserParticipantDto {

    private long id;

    private String username;

    private String email;
}
