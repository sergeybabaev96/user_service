package school.faang.user_service.dto.preson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.Country;

@Builder
@Data
public class PersonContactDto {

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private Country country;
}
