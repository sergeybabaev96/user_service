package school.faang.user_service.controller.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.rating.RatingService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/ratings")
@RestController
public class UserRatingController {

    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<List<Long>> getTopUsers() {
        return ResponseEntity.ok(ratingService.getTopUsers());
    }
}
