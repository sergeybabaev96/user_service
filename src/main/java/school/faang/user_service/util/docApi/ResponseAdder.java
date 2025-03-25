package school.faang.user_service.util.docApi;


import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;


@AllArgsConstructor
@Builder
public class ResponseAdder {
    @NotBlank
    private String version;
    @NotBlank
    private String code;
    @NotBlank
    private String description;
    private Object example;

    public void addInto(ApiResponses responses) {
        MediaType mediaType = example != null ? new MediaType().example(example) : new MediaType();

        responses.addApiResponse(code, new ApiResponse()
                .description(description)
                .content(new Content()
                        .addMediaType(version, mediaType)));
    }
}
