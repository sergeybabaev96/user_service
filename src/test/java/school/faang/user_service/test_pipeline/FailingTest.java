package school.faang.user_service.test_pipeline;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;

public class FailingTest {
    @Test
    public void testFailure() {
        fail("Намеренный провал теста для проверки CI");
    }
}