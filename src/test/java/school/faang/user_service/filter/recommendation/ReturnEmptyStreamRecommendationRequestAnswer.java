package school.faang.user_service.filter.recommendation;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class ReturnEmptyStreamRecommendationRequestAnswer implements Answer<Stream<RecommendationRequest>> {

    @Override
    public Stream<RecommendationRequest> answer(InvocationOnMock invocationOnMock) throws Throwable {
        return Stream.empty();
    }
}
