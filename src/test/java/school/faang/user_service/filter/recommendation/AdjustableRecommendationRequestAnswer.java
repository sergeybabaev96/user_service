package school.faang.user_service.filter.recommendation;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class AdjustableRecommendationRequestAnswer implements Answer<Stream<RecommendationRequest>> {
    private final Predicate<RecommendationRequest> filter;

    // Почему-то в тестах Mockito не видит конструктор, созданный с помощью аннотации @RequiredArgsConstructor
    public AdjustableRecommendationRequestAnswer(Predicate<RecommendationRequest> filter) {
        this.filter = filter;
    }

    @Override
    public Stream<RecommendationRequest> answer(InvocationOnMock invocationOnMock) throws Throwable {
        Stream<RecommendationRequest> source = invocationOnMock.getArgument(0);

        return source.filter(filter);
    }
}
