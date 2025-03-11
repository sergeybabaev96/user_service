package school.faang.user_service.filter.Event;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.event.Event;

@RequiredArgsConstructor
public class TitleFilter implements EventFilter{
    private final  String title;

    @Override
    public boolean matches(Event event) {
        return title == null || event.getTitle().toLowerCase().contains(title.toLowerCase());
    }
}
