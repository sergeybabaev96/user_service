package school.faang.user_service.dto.user;

import lombok.Data;

@Data
public class UserFilterDto {

    private Long countryId;

    private Boolean active;
}
