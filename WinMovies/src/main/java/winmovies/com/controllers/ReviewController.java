package winmovies.com.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import winmovies.com.controllers.vos.NewReviewVO;
import winmovies.com.controllers.vos.UpdateReviewVO;
import winmovies.com.models.Review;
import winmovies.com.services.ReviewService;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final JmsTemplate jmsTemplate;

    public ReviewController(ReviewService reviewService, JmsTemplate jmsTemplate) {
        this.reviewService = reviewService;
        this.jmsTemplate = jmsTemplate;
    }

    @GetMapping
    public ResponseEntity<Flux<Review>> findAll() {
        Flux<Review> reviews = reviewService.findAll();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<Review>> findById(@PathVariable final String id) {
        Mono<Review> review = reviewService.findById(id);
        return review != null ?
                ResponseEntity.ok(review) : ResponseEntity.notFound().build();
    }

    @GetMapping("/byUser/{userId}")
    public ResponseEntity<Flux<Review>> findByUserId(@PathVariable final String userId) {
        Flux<Review> reviews = reviewService.findByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity saveReview(@RequestBody final NewReviewVO newReviewVO) {
        this.reviewService.newReview(newReviewVO);
        jmsTemplate.convertAndSend("reviews", "Saved review: " + newReviewVO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity updateReview(@RequestBody final UpdateReviewVO updateReviewVO) {
        this.reviewService.updateReview(updateReviewVO);
        jmsTemplate.convertAndSend("reviews", "Saved review: " + updateReviewVO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteReview(@PathVariable final String id) {
        this.reviewService.deleteReview(id);
        jmsTemplate.convertAndSend("reviews", "Deleted review ID: " + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
