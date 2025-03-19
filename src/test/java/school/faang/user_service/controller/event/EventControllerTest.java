package school.faang.user_service.controller.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.databuilder.event.EventDtoBuilder;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.Event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventController.class)
@ContextConfiguration(classes = EventController.class)
@DisplayName("EventController MVC Tests")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    private static final long EVENT_ID = 1L;
    private static final long NON_EXISTING_EVENT_ID = 999L;
    private static final long USER_ID = 1L;
    private static final LocalDateTime BASELINE_TIME = LocalDateTime.now().plusYears(1);

    @Nested
    @DisplayName("POST /events")
    class CreateEventTests {

        @Test
        @DisplayName("Should create event and return 201 with correct Location header")
        void testCreateValidEvent() throws Exception {
            EventDto request = EventDtoBuilder.createValidEventDto(null);
            EventDto response = EventDtoBuilder.createValidEventDto(EVENT_ID);
            when(eventService.create(any(EventDto.class))).thenReturn(response);

            mockMvc.perform(post("/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/events/" + EVENT_ID))
                    .andExpect(jsonPath("$.id").value(EVENT_ID))
                    .andExpect(jsonPath("$.title").value("Java presentation"));
        }

        @Test
        @DisplayName("Should validate event via EventDtoValidator on create")
        void testCreateEventCallsValidator() throws Exception {
            EventDto request = EventDtoBuilder.createValidEventDto(null);
            when(eventService.create(any())).thenReturn(EventDtoBuilder.createValidEventDto(EVENT_ID));

            mockMvc.perform(post("/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            verify(eventService).create(any());
        }

        @Test
        @DisplayName("Should return 500 when validation fails (startDate in past)")
        void testCreateEventInvalidStartDate() throws Exception {
            EventDto invalidRequest = EventDtoBuilder.createInvalidEventDto();

            mockMvc.perform(post("/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(jsonPath("$.message").value("Validation failed: StartDate can't be null or in the past"));
        }
    }

    @Nested
    @DisplayName("GET /events/{eventId}")
    class GetEventTests {

        @Test
        @DisplayName("Should return event for existing ID")
        void testGetEventExistingId() throws Exception {
            EventDto dto = EventDtoBuilder.createValidEventDto(EVENT_ID);
            when(eventService.getEvent(EVENT_ID)).thenReturn(dto);

            mockMvc.perform(get("/events/{eventId}", EVENT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(EVENT_ID))
                    .andExpect(jsonPath("$.title").value("Java presentation"));
        }

        @Test
        @DisplayName("Should throw DataValidationException for non-existing event")
        void testGetEventNonExistingId() {
            when(eventService.getEvent(NON_EXISTING_EVENT_ID))
                    .thenThrow(new DataValidationException("Event with id not found"));

            assertThrows(ServletException.class, () ->
                    mockMvc.perform(get("/events/{eventId}", NON_EXISTING_EVENT_ID))
            );
        }
    }

    @Nested
    @DisplayName("PUT /events/{eventId}")
    class UpdateEventTests {

        @Test
        @DisplayName("Should update event and return 200 with updated data")
        void testUpdateEventValidData() throws Exception {
            EventDto request = EventDtoBuilder.createValidEventDto(EVENT_ID);
            when(eventService.updateEvent(any(EventDto.class), any(Long.class))).thenReturn(request);

            mockMvc.perform(put("/events/{eventId}", EVENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(EVENT_ID))
                    .andExpect(jsonPath("$.title").value("Java presentation"));
        }

        @Test
        @DisplayName("Should validate event via EventDtoValidator on update")
        void testUpdateEventCallsValidator() throws Exception {
            EventDto request = EventDtoBuilder.createValidEventDto(EVENT_ID);
            when(eventService.updateEvent(any(), eq(EVENT_ID))).thenReturn(request);

            mockMvc.perform(put("/events/{eventId}", EVENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(eventService).updateEvent(any(), eq(EVENT_ID));
        }

        @Test
        @DisplayName("Should return 400 when ID mismatch")
        void testUpdateEventIdMismatch() throws Exception {
            EventDto request = EventDtoBuilder.createInvalidEventDto();

            mockMvc.perform(put("/events/{eventId}", EVENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(jsonPath("$.message").value("Validation failed: StartDate can't be null or in the past"));
        }
    }

    @Nested
    @DisplayName("DELETE /events/{eventId}")
    class DeleteEventTests {

        @Test
        @DisplayName("Should delete event and return 204")
        void testDeleteEventExistingId() throws Exception {
            doNothing().when(eventService).deleteEvent(EVENT_ID);

            mockMvc.perform(delete("/events/{eventId}", EVENT_ID))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("GET /events/filter")
    class GetEventsByFilterTests {

        @Test
        @DisplayName("Should return filtered list of events")
        void testGetEventsByFilter() throws Exception {
            List<EventDto> expected = List.of(
                    EventDtoBuilder.createValidEventDto(1L),
                    EventDtoBuilder.createValidEventDto(2L)
            );

            EventFilterDto filter = new EventFilterDto();
            filter.setTitle("Java");
            filter.setStartDate(BASELINE_TIME.plusDays(1));
            filter.setEndDate(BASELINE_TIME.plusDays(5));
            filter.setOwnerId(USER_ID);

            when(eventService.getEventsByFilter(any())).thenReturn(expected);

            mockMvc.perform(get("/events/filter")
                            .param("title", filter.getTitle())
                            .param("startDate", filter.getStartDate().toString())
                            .param("endDate", filter.getEndDate().toString())
                            .param("ownerId", filter.getOwnerId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[1].id").value(2L));
        }
    }

    @Nested
    @DisplayName("GET /events/owned/{userId}")
    class GetOwnedEventsTests {

        @Test
        @DisplayName("Should return events owned by user")
        void testGetOwnedEvents() throws Exception {
            List<EventDto> expected = List.of(EventDtoBuilder.createValidEventDto(EVENT_ID));
            when(eventService.getOwnedEvents(USER_ID)).thenReturn(expected);

            mockMvc.perform(get("/events/owned/{userId}", USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(EVENT_ID));
        }
    }

    @Nested
    @DisplayName("GET /events/participated/{userId}")
    class GetParticipatedEventsTests {

        @Test
        @DisplayName("Should return events in which user participates")
        void testGetParticipatedEvents() throws Exception {
            List<EventDto> expected = List.of(EventDtoBuilder.createValidEventDto(EVENT_ID));
            when(eventService.getParticipatedEvents(USER_ID)).thenReturn(expected);

            mockMvc.perform(get("/events/participated/{userId}", USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(EVENT_ID));
        }
    }
}
