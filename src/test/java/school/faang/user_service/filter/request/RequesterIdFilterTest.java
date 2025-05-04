package school.faang.user_service.filter.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class RequesterIdFilterTest extends UserIdFilterTest {

    @InjectMocks
    private RequesterIdFilter requesterIdFilter;

    @Test
    void testIsApplicableWhenRequesterIdExistsShouldReturnTrue() {
        testIsApplicableShouldReturnTrueWhenUserIdIsPresent(requesterIdFilter);
    }

    @Test
    void testIsApplicableWhenRequesterIdNotExistsShouldReturnFalse() {
        testIsApplicableShouldReturnFalseWhenUserIdIsNull(requesterIdFilter);
    }

    @Test
    void testApplyShouldFilterRequestsByRequesterId() {
        testApplyShouldFilterRequestsByUserId(
                requesterIdFilter,
                List.of(
                        () -> request1.getRequester(),
                        () -> request2.getRequester()
                )
        );
    }

    @Test
    void testApplyWhenNoMatchesShouldReturnEmptyStream() {
        testApplyWhenNoMatchesShouldReturnEmptyStream(requesterIdFilter, () -> request1.getRequester());
    }

    @Test
    void testApplyWhenEmptyStreamShouldReturnEmptyStream() {
        testApplyWhenEmptyStreamShouldReturnEmptyStream(requesterIdFilter);
    }
}
