package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;


class MentorshipMapperTest {

    private final MentorshipMapper mapper = new MentorshipMapperImpl();

    @Test
    void testToDto() {
        User user = User.builder()
                .id(1L)
                .username("user")
                .build();

        MentorshipDto dto = mapper.toDto(user);

        assertEquals(user.getId(), dto.getUserId(), "User ID should match DTO userId");
        assertEquals(user.getUsername(), dto.getUserName(), "Username should match DTO userName");
    }

    @Test
    void testToEntity() {
        MentorshipDto dto = new MentorshipDto();
        dto.setUserId(1L);
        dto.setUserName("user");

        User user = mapper.toEntity(dto);

        assertEquals(user.getId(), dto.getUserId(), "User ID should match DTO userId");
        assertEquals(user.getUsername(), dto.getUserName(), "Username should match DTO userName");
    }

    @Test
    void testToDtos() {
        List<User> users = List.of(
                User.builder().id(1L).username("user1").build(),
                User.builder().id(2L).username("user2").build()
        );

        List<MentorshipDto> dtos = mapper.toDtos(users);

        assertThat(dtos).hasSize(2)
                .extracting(MentorshipDto::getUserId, MentorshipDto::getUserName)
                .containsExactlyInAnyOrder(
                        tuple(1L, "user1"),
                        tuple(2L, "user2")
                );
    }
}