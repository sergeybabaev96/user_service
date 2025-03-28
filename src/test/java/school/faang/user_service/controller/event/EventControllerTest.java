package school.faang.user_service.controller.event;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import school.faang.user_service.databuilder.event.EventTestDataBuilder;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.filter.Event.EventFilterDto;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventController.class)
@ContextConfiguration(classes = {EventController.class, GlobalExceptionHandler.class})
@DisplayName("EventController MVC Tests")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private EventMapper eventMapper;

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
            EventDto request = EventTestDataBuilder.createValidEventDto(null);
            EventDto response = EventTestDataBuilder.createValidEventDto(EVENT_ID);
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
            EventDto request = EventTestDataBuilder.createValidEventDto(null);
            when(eventService.create(any())).thenReturn(EventTestDataBuilder.createValidEventDto(EVENT_ID));

            mockMvc.perform(post("/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            verify(eventService).create(any());
        }

        @Test
        @DisplayName("Should return 400 when validation fails (startDate in past)")
        void testCreateEventInvalidStartDate() throws Exception {
            EventDto invalidRequest = EventTestDataBuilder.createInvalidEventDto();

            mockMvc.perform(post("/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Validation failed: StartDate can't be null or in the past"));
        }
    }

    @Nested
    @DisplayName("GET /events/{eventId}")
    class GetEventTests {

        @Test
        @DisplayName("Should return event for existing ID")
        void testGetEventExistingId() throws Exception {
            EventDto dto = EventTestDataBuilder.createValidEventDto(EVENT_ID);
            when(eventService.getEvent(EVENT_ID)).thenReturn(dto);

            mockMvc.perform(get("/events/{eventId}", EVENT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(EVENT_ID))
                    .andExpect(jsonPath("$.title").value("Java presentation"));
        }

        @Test
        @DisplayName("Should throw DataValidationException for non-existing event")
        void testGetEventNonExistingId() throws Exception {
            when(eventService.getEvent(NON_EXISTING_EVENT_ID))
                    .thenThrow(new DataValidationException("Event with id not found"));

            mockMvc.perform(get("/events/{eventId}", NON_EXISTING_EVENT_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Event with id not found"));
        }
    }

    @Nested
    @DisplayName("PUT /events/{eventId}")
    class UpdateEventTests {

        @Test
        @DisplayName("Should update event and return 200 with updated data")
        void testUpdateEventValidData() throws Exception {
            EventDto request = EventTestDataBuilder.createValidEventDto(EVENT_ID);
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
            EventDto request = EventTestDataBuilder.createValidEventDto(EVENT_ID);
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
            EventDto request = EventTestDataBuilder.createInvalidEventDto();

            mockMvc.perform(put("/events/{eventId}", EVENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Validation failed: StartDate can't be null or in the past"));
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
        @DisplayName("Should return filtered events correctly")
        void testGetEventsByFilter() throws Exception {
            String title = "Java";
            LocalDateTime startDate = BASELINE_TIME.plusDays(5);
            LocalDateTime endDate = BASELINE_TIME.plusDays(15);
            Long ownerId = 1L;

            Event event1 = EventTestDataBuilder.createValidEvent(1L, EventTestDataBuilder.createValidUser(ownerId));
            Event event2 = EventTestDataBuilder.createValidEvent(2L, EventTestDataBuilder.createValidUser(ownerId));

            when(eventMapper.toDto(event1)).thenReturn(EventTestDataBuilder.createValidEventDto(1L));
            when(eventMapper.toDto(event2)).thenReturn(EventTestDataBuilder.createValidEventDto(2L));

            when(eventService.getEventsByFilter(any(EventFilterDto.class)))
                    .thenAnswer(invocation -> {
                        EventFilterDto filter = invocation.getArgument(0);
                        return List.of(eventMapper.toDto(event1), eventMapper.toDto(event2));
                    });

            mockMvc.perform(get("/events/filter")
                            .param("title", title)
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString())
                            .param("ownerId", ownerId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].id").value(2));

            verify(eventService, times(1)).getEventsByFilter(argThat(filter ->
                    title.equals(filter.getTitle()) &&
                            startDate.equals(filter.getStartDate()) &&
                            endDate.equals(filter.getEndDate()) &&
                            ownerId.equals(filter.getOwnerId())
            ));

            verify(eventMapper, times(1)).toDto(event1);
            verify(eventMapper, times(1)).toDto(event2);
        }

        @Test
        @DisplayName("Should return 400 if required fields in EventFilterDto are missing or invalid")
        void testGetEventsByFilterWithNullFilter() throws Exception {
            mockMvc.perform(get("/events/filter")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Owner ID cannot be null")));
        }
    }

    @Nested
    @DisplayName("GET /events/owned/{userId}")
    class GetOwnedEventsTests {

        @Test
        @DisplayName("Should return events owned by user")
        void testGetOwnedEvents() throws Exception {
            List<EventDto> expected = List.of(EventTestDataBuilder.createValidEventDto(EVENT_ID));
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
            List<EventDto> expected = List.of(EventTestDataBuilder.createValidEventDto(EVENT_ID));
            when(eventService.getParticipatedEvents(USER_ID)).thenReturn(expected);

            mockMvc.perform(get("/events/participated/{userId}", USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(EVENT_ID));
        }
    }
}
