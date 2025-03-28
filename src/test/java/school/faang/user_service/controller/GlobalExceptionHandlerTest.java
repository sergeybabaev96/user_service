package school.faang.user_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityAlreadyExistException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorResponse;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private ErrorResponse errorResponse;

    @Test
    @DisplayName("Проверка обработки исключения DataValidationException")
    void givenDataValidationHandler_WhenHandleDataValidationException_ThenReturnErrorResponse() {
        errorResponse = globalExceptionHandler.handleDataValidationException(
                new DataValidationException("Data validation exception"));

        assertEquals("Data validation exception", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Проверка обработки исключения EntityAlreadyExistException")
    void givenEntityAlreadyExistHandler_WhenHandleEntityAlreadyExistException_ThenReturnErrorResponse() {
        errorResponse = globalExceptionHandler.handleEntityAlreadyExistException(
                new EntityAlreadyExistException("Entity already exist"));

        assertEquals("Entity already exist", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Проверка обработки исключения EntityNotFoundException")
    void givenEntityNotFoundHandler_WhenHandleEntityNotFoundException_ThenReturnErrorResponse() {
        errorResponse = globalExceptionHandler.handleEntityNotFoundException(
                new EntityNotFoundException("Entity not found exception"));

        assertEquals("Entity not found exception", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Проверка обработки исключения RuntimeException")
    void givenRuntimeHandler_WhenHandleRuntimeException_ThenReturnErrorResponse() {
        errorResponse = globalExceptionHandler.handleRuntimeException(
                new RuntimeException("Unchecked exception"));

        assertEquals("Unchecked exception", errorResponse.getMessage());
    }
}