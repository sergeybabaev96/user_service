package school.faang.user_service.config.docApi;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.util.docApi.MethodDocumentor;
import school.faang.user_service.util.docApi.ResponseAdder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class EventParticipationConfig {
    private final ResponseAdder invalidIdResponse = ResponseAdder.builder()
            .version("application/v1/json")
            .code("400")
            .description("Ошибка валидации")
            .example("Id is less than zero")
            .build();

    private final Parameter eventId = new Parameter()
            .name("eventId")
            .description("ID события")
            .example("1")
            .in("path")
            .required(true);

    private final Parameter userId = new Parameter()
            .name("userId")
            .description("ID пользователя")
            .example("123")
            .in("path")
            .required(true);

    private final String getParticipantsSuccessResponse =
            """
                    [
                        {
                                "id": 2,
                                "username": "JaneSmith",
                                "email": "janesmith@example.com",
                                "mentorIds": null,
                                "menteeIds": null
                            },
                            {
                                "id": 3,
                                "username": "IvanPetrovich",
                                "email": "vanok@example.com",
                                "mentorIds": null,
                                "menteeIds": null
                            }
                        ]
                    """;

    private final Map<String, Consumer<Operation>> methodCustomizers = Map.of(
            "registerParticipant", this::customizeRegisterParticipant,
            "unregisterParticipant", this::customizeUnregisterParticipant,
            "getParticipants", this::customizeGetParticipants,
            "getParticipantsCount", this::customizeGetParticipantsCount
    );

    @Bean
    public OperationCustomizer eventParticipationOperationCustomizer() {
        return (operation, handlerMethod) -> {
            if (handlerMethod.getBeanType().equals(EventParticipationController.class)) {
                operation.setTags(List.of("Управление участием пользователей в событиях"));

                Optional.ofNullable(methodCustomizers.get(handlerMethod.getMethod().getName()))
                        .ifPresent(customizer -> customizer.accept(operation));
            }
            return operation;
        };
    }


    private void customizeRegisterParticipant(Operation operation) {
        MethodDocumentor.builder()
                .operation(operation)
                .summary("Регистрация участника")
                .description("Регистрирует пользователя для участия в событии")
                .parameters(List.of(eventId, userId))
                .responses(List.of(
                        ResponseAdder.builder()
                                .version("application/v1/json")
                                .code("200")
                                .description("Успешная регистрация")
                                .build(),
                        ResponseAdder.builder()
                                .version("application/v1/json")
                                .code("400")
                                .description("Ошибка валидации")
                                .example("User is already registered for this event")
                                .build()))
                .build()
                .documentMethod();
    }


    public void customizeUnregisterParticipant(Operation operation) {
        MethodDocumentor.builder()
                .operation(operation)
                .summary("Отмена регистрации участника")
                .description("Отменяет участие пользователя в событии")
                .parameters(List.of(eventId, userId))
                .responses(List.of(
                        ResponseAdder.builder()
                                .version("application/v1/json")
                                .code("200")
                                .description("Отмена регистрации")
                                .build(),
                        ResponseAdder.builder()
                                .version("application/v1/json")
                                .code("400")
                                .description("Ошибка валидации")
                                .example("User is not registered for this event")
                                .build()))
                .build()
                .documentMethod();
    }

    public void customizeGetParticipants(Operation operation) {
        MethodDocumentor.builder()
                .operation(operation)
                .summary("Получить участников")
                .description("Возвращает список всех участников события")
                .parameters(List.of(eventId))
                .responses(List.of(
                        ResponseAdder.builder()
                                .version("application/v1/json")
                                .code("200")
                                .description("Получение участников")
                                .example(getParticipantsSuccessResponse)
                                .build(),
                        invalidIdResponse))
                .build()
                .documentMethod();
    }

    public void customizeGetParticipantsCount(Operation operation) {
        MethodDocumentor.builder()
                .operation(operation)
                .summary("Получить количество участников")
                .description("Возвращает количество участников")
                .parameters(List.of(eventId))
                .responses(List.of(
                        ResponseAdder.builder()
                                .version("application/v1/json")
                                .code("200")
                                .description("Получение количества участников")
                                .example("2")
                                .build(),
                        invalidIdResponse))
                .build()
                .documentMethod();
    }
}