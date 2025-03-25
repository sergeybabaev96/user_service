package school.faang.user_service.util.docApi;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Builder
@AllArgsConstructor
public class MethodDocumentor {
    @NotNull
    private Operation operation;
    @NotNull
    private String summary;
    @NotNull
    private String description;
    private List<Parameter> parameters;
    @NotNull
    private List<ResponseAdder> responses;

    public void documentMethod() {
        operation
                .summary(summary)
                .description(description)
                .parameters(parameters);
        responses.forEach(response -> response.addInto(operation.getResponses()));
    }
}
