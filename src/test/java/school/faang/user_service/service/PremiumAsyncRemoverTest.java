package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.PremiumAsyncRemover;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PremiumAsyncRemoverTest {
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private PremiumAsyncRemover premiumAsyncRemover;


    @Test
    @DisplayName("Удаление премиумов вызывается через TransactionTemplate")
    public void testRemoveExpiredPremiumAsync() {
        doAnswer(invocation -> {
            Consumer<TransactionStatus> consumer = invocation.getArgument(0);
            consumer.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(ArgumentMatchers.any());

        premiumAsyncRemover.removeExpiredPremiumAsync(anyList());

        verify(premiumRepository, times(1)).deleteAll(anyList());
        verify(transactionTemplate, times(1)).executeWithoutResult(ArgumentMatchers.any());
    }
}

