package school.faang.user_service.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.PremiumAsyncRemover;

@ExtendWith(MockitoExtension.class)
public class PremiumAsyncRemoverTest {
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private PremiumAsyncRemover premiumAsyncRemover;
}
