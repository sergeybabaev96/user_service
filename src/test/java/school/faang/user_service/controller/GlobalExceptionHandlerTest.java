package school.faang.user_service.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityAlreadyExistException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorResponse;


@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private ResponseEntity<ErrorResponse> responseEntity;

    @Test
    @DisplayName("Проверка обработки исключения DataValidationException")
    void givenDataValidationHandler_WhenHandleDataValidationException_ThenReturnErrorResponse() {
        responseEntity = globalExceptionHandler.handleDataValidationException(
                new DataValidationException("Data validation exception"));

        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Проверка обработки исключения EntityAlreadyExistException")
    void givenEntityAlreadyExistHandler_WhenHandleEntityAlreadyExistException_ThenReturnErrorResponse() {
        responseEntity = globalExceptionHandler.handleEntityAlreadyExistException(
                new EntityAlreadyExistException("Entity already exist"));

        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Проверка обработки исключения EntityNotFoundException")
    void givenEntityNotFoundHandler_WhenHandleEntityNotFoundException_ThenReturnErrorResponse() {
        responseEntity = globalExceptionHandler.handleEntityNotFoundException(
                new EntityNotFoundException("Entity not found exception"));

        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Проверка обработки исключения RuntimeException")
    void givenRuntimeHandler_WhenHandleRuntimeException_ThenReturnErrorResponse() {
        responseEntity = globalExceptionHandler.handleRuntimeException(
                new RuntimeException("Unchecked exception"));

        assertTrue(responseEntity.getStatusCode().is5xxServerError());
    }
}