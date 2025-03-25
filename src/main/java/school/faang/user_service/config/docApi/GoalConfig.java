package school.faang.user_service.config.docApi;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.util.docApi.MethodDocumentor;
import school.faang.user_service.util.docApi.ResponseAdder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Configuration
public class GoalConfig {
    private final Parameter goalId = new Parameter()
            .name("goalId")
            .description("ID события")
            .example("1")
            .in("path")
            .required(true);

    private final Parameter userId = new Parameter()
            .name("userId")
            .description("ID пользователя")
            .example("123")
            .in("request param")
            .required(true);

    private final Parameter titlePattern = new Parameter()
            .name("titlePattern")
            .description("паттерн сортировки целей")
            .example("title")
            .in("request param");

    private final Parameter descriptionPattern = new Parameter()
            .name("descriptionPattern")
            .description("паттерн сортировки целей")
            .example("description")
            .in("request param");

    private final GoalDto requestGoal = GoalDto.builder()
            .title("title")
            .description("description")
            .parentId(1L)
            .skillIds(List.of(1L, 2L, 3L))
            .build();

    private final GoalDto responseGoal = GoalDto.builder()
            .id(2L)
            .title("title")
            .description("description")
            .parentId(1L)
            .skillIds(List.of(1L, 2L, 3L))
            .build();

    private final Map<String, Consumer<Operation>> methodCustomizers = Map.of(
            "createGoal", this::customizeCreateGoal,
            "updateGoal", this::customizeUpdateGoal,
            "deleteGoal", this::customizeDeleteGoal,
            "getSubtasksByGoalId", this::customizeGetSubtasksByGoalId,
            "getGoalsByUserId", this::customizeGetGoalsByUserId
    );

    @Bean
    public OperationCustomizer goalOperationCustomizer() {
        return (operation, handlerMethod) -> {
            if (handlerMethod.getBeanType().equals(GoalController.class)) {
                operation.setTags(List.of("Управление целями пользователей"));

                Optional.ofNullable(methodCustomizers.get(handlerMethod.getMethod().getName()))
                        .ifPresent(customizer -> customizer.accept(operation));
            }
            return operation;
        };
    }

    public void customizeCreateGoal(Operation operation) {
        MethodDocumentor.builder()
                .operation(operation)
                .summary("Создать цель")
                .description("Создает цель для пользователя")
                .parameters(List.of(userId))
                .responses(List.of(
                        ResponseAdder.builder()
                                .version("application/v1/json")
                                .code("200")
                                .description("Создание цели")
                                .example(responseGoal)
                                .build()
                        ))
                .build()
                .documentMethod();
    }

    public void customizeUpdateGoal(Operation operation) {
        MethodDocumentor.builder()
                .operation(operation)
                .summary("Изменить цель")
                .description("Изменяет цель пользователя")
                .parameters(List.of(goalId))
                .responses(List.of(
                        ResponseAdder.builder()
                                .version("application/v1/json")
                                .code("200")
                                .description("Изменение цели")
                                .example(responseGoal)
                                .build()
                ))
                .build()
                .documentMethod();
    }

    public void customizeDeleteGoal(Operation operation) {
        MethodDocumentor.builder()
                .operation(operation)
                .summary("Удалить цель")
                .description("Удаляет цель пользователя")
                .parameters(List.of(goalId))
                .responses(List.of(
                        ResponseAdder.builder()
                                .version("application/v1/json")
                                .code("200")
                                .description("Удаление цели")
                                .build()
                ))
                .build()
                .documentMethod();
    }

    public void customizeGetSubtasksByGoalId(Operation operation) {
        MethodDocumentor.builder()
                .operation(operation)
                .summary("Получить все подцели у цели")
                .description("Получает все подцели у цели")
                .parameters(List.of(goalId, titlePattern, descriptionPattern))
                .responses(List.of(
                        ResponseAdder.builder()
                                .version("application/v1/json")
                                .code("200")
                                .description("Получение подцели")
                                .example(List.of(responseGoal))
                                .build()
                ))
                .build()
                .documentMethod();
    }

    public void customizeGetGoalsByUserId(Operation operation) {
        MethodDocumentor.builder()
                .operation(operation)
                .summary("Получить все цели пользователя")
                .description("Возвращает все цели пользователя")
                .parameters(List.of(userId, titlePattern, descriptionPattern))
                .responses(List.of(
                        ResponseAdder.builder()
                                .version("application/v1/json")
                                .code("200")
                                .description("Получение всех целей")
                                .example(List.of(responseGoal))
                                .build()))
                .build()
                .documentMethod();
    }
}
