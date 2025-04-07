package school.faang.user_service.dto.preson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PersonAboutDto {

    @JsonProperty("state")
    private String state;

    @JsonProperty("faculty")
    private String faculty;

    @JsonProperty("major")
    private String major;

    @JsonProperty("employer")
    private String employer;

    @JsonProperty("yearOfStudy")
    private String yearOfStudy;
}
