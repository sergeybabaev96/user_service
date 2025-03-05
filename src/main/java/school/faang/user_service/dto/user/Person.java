package school.faang.user_service.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Person (
        @JsonProperty("firstName")
        String firstName,

        @JsonProperty("lastName")
        String lastName
){}
