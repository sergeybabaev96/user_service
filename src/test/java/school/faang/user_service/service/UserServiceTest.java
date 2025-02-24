package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private User testUser;
    private User testUser2;
    private User testUser3;
    private List<Long> testUsersIds;
    private List<User> testUsers;
    private static final Long USER_ID = 1L;
    private static final Long USER2_ID = 2L;
    private static final Long USER3_ID = 3L;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(USER_ID).build();
        testUser2 = User.builder().id(USER2_ID).build();
        testUser3 = User.builder().id(USER3_ID).build();
        testUsersIds = List.of(USER_ID, USER2_ID, USER3_ID);
        testUsers = List.of(testUser, testUser2, testUser3);
    }

    @Test
    void getUserShouldReturnUserWhenUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

        User result = userService.getUser(USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void getUserShouldThrowExceptionWhenUserNotExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userService.getUser(USER_ID));

        assertEquals(String.format("User with id #%d not found", USER_ID), exception.getMessage());

        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void getUserNotCallRepositoryForNullId() {
        Long nullUserId = null;

        assertThrows(NoSuchElementException.class, () -> userService.getUser(nullUserId));
    }

    @Test
    void getUsersReturnUsers() {
        Mockito.when(userRepository.findAllById(testUsersIds)).thenReturn(testUsers);

        List<User> result = userService.getUsers(testUsersIds);

        Assertions.assertEquals(testUsers, result);
        Mockito.verify(userRepository, times(1)).findAllById(testUsersIds);
    }

    @Test
    void getUsersByIdsOrderedReturnUsersInSameOrder() {
        List<Long> orderedIds = List.of(3L, 1L,2L);
        List<User> testOrderedUsers = List.of(testUser3, testUser, testUser2);
        Mockito.when(userRepository.findAllById(orderedIds)).thenReturn(testUsers);

        List<User> result = userService.getUsersByIdsInGivenOrder(orderedIds);

        Assertions.assertEquals(testOrderedUsers.get(0).getId(), result.get(0).getId());
        Assertions.assertEquals(testOrderedUsers.get(1).getId(), result.get(1).getId());
        Assertions.assertEquals(testOrderedUsers.get(2).getId(), result.get(2).getId());
        Mockito.verify(userRepository, times(1)).findAllById(orderedIds);
    }
}