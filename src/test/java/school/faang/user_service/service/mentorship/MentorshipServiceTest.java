package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private UserMapperImpl userMapperIml;


    private Long id = 2L;
    private Long id2 = 4L;
    private Long id3 = 3L;
    private Long id4 = 5L;

    private User user = new User();
    private User user2 = new User();
    private User user3 = new User();
    private User user4 = new User();

    private List<User> users;

    private Optional<User> optionalUser;


    @BeforeEach
    public void setUp() {
        user2.setId(id2);
        user3.setId(id3);
        user4.setId(id4);
        users = List.of(user2, user3, user4);

        user.setId(id);
        user.setMentees(users);

        optionalUser = Optional.of(user);
    }


    @Test
    public void testGetMentorshipNotNull() {
        when(mentorshipRepository.findById(id)).thenReturn(optionalUser);
        UserDto userDto = userMapperIml.toUserDto(optionalUser.get());

        when(mentorshipRepository.findAllById(userDto.getMenteesIds())).thenReturn(users);

        assertEquals(user.getMentees().size(), mentorshipService.getMentees(user.getId()).size());

    }

    @Test
    public void testGetMentorshipNull() {
        when(mentorshipRepository.findById(id)).thenReturn(optionalUser);

        UserDto userDto = null;

        assertTrue(mentorshipService.getMentees(user.getId()).isEmpty());
    }
}
