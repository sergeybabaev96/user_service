package school.faang.user_service.failing;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;

public class FailingTest {
    @Test
    void thisTestShouldFail() {
        fail("This test is intentionally failing.");
    }
}
