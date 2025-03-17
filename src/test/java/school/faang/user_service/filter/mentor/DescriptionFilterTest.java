package school.faang.user_service.filter.mentor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DescriptionFilterTest {
    private final DescriptionFilter descriptionFilter = new DescriptionFilter();
    private RequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        requestFilterDto = new RequestFilterDto();
    }

    //Positive
    @Test
    void isApplicable() {
        //Arrange
        requestFilterDto.setDescription("Text");

        //Assert
        assertTrue(descriptionFilter.isApplicable(requestFilterDto));
    }

    @Test
    void apply() {
        //Arrange
        requestFilterDto.setDescription("Java");
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setDescription("Java");
        request2.setDescription("C#");
        List<MentorshipRequest> requests = List.of(request1, request2);

        //Act
        List<MentorshipRequest> result = descriptionFilter.apply(requests.stream(), requestFilterDto).toList();

        //Assert
        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getDescription());
    }

    @Test
    void applyCheckRegister() {
        //Arrange
        requestFilterDto.setDescription("jAvA");
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setDescription("Java");
        request2.setDescription("java");
        List<MentorshipRequest> requests = List.of(request1, request2);

        //Act
        List<MentorshipRequest> result = descriptionFilter.apply(requests.stream(), requestFilterDto).toList();

        //Assert
        assertEquals(2, result.size());
        assertEquals("java", result.get(0).getDescription().toLowerCase());
        assertEquals("java", result.get(1).getDescription().toLowerCase());
    }

    //Negative
    @Test
    void isApplicableNullDescription() {
        //Arrange
        requestFilterDto.setDescription(null);

        //Assert
        assertFalse(descriptionFilter.isApplicable(requestFilterDto));
    }

    @Test
    void isApplicableBlankDescription() {
        //Arrange
        requestFilterDto.setDescription("");

        //Assert
        assertFalse(descriptionFilter.isApplicable(requestFilterDto));
    }

    @Test
    void applyEmptyListWhenNoMatches() {
        //Arrange
        requestFilterDto.setDescription("Java");
        MentorshipRequest request1 = new MentorshipRequest();
        request1.setDescription("C# Developer");
        List<MentorshipRequest> requests = List.of(request1);

        //Act
        List<MentorshipRequest> result = descriptionFilter.apply(requests.stream(), requestFilterDto).toList();

        //Assert
        assertTrue(result.isEmpty());
    }
}