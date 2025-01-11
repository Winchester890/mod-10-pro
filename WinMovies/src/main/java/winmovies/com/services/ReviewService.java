package winmovies.com.services;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import winmovies.com.controllers.vos.NewReviewVO;
import winmovies.com.controllers.vos.UpdateReviewVO;
import winmovies.com.models.Review;
import winmovies.com.models.User;
import winmovies.com.repositories.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ReviewMessageReceiver reviewMessageReceiver;

    public ReviewService(ReviewRepository reviewRepository, UserService userService, ReviewMessageReceiver reviewMessageReceiver) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.reviewMessageReceiver = reviewMessageReceiver;
    }

    public Flux<Review> findAll() {
        return this.reviewRepository.findAll();
    }

    public Mono<Review> findById(final String id) {
        return this.reviewRepository.findById(id);
    }

    public Flux<Review> findByUserId(final String userId) {
        return this.reviewRepository.findByUserId(userId);
    }

    public void newReview(final NewReviewVO newReviewVO) {
        if (!isRatingValid(newReviewVO.getRating())) {
            throw new RuntimeException("Invalid rating!");
        } else {
            String userId = newReviewVO.getUserId();
            String movieTitle = newReviewVO.getMovieTitle();

            Mono<Review> reviewAlreadyRatedByUser = movieAlreadyRatedByUser(userId, movieTitle);

            reviewAlreadyRatedByUser.flatMap(review -> {
                review.setRating(newReviewVO.getRating());
                review.setComment(newReviewVO.getComment());
                return Mono.just(review);
            }).switchIfEmpty(Mono.defer(() -> {
                return newReviewFromReviewVO(newReviewVO);
            })).subscribe(reviewMessageReceiver::receiveReviewToSave);
        }
    }

    public void updateReview(final UpdateReviewVO updateReviewVO) {
        if (!isRatingValid(updateReviewVO.getRating())) {
            throw new RuntimeException("Invalid rating!");
        } else {
            Mono<User> userMono = this.userService.findById(updateReviewVO.getUserId());
            userMono.subscribe(user -> {
                Review review = mapToUpdateReview(user, updateReviewVO);
                reviewMessageReceiver.receiveReviewToSave(review);
            });
        }
    }

    public void deleteReview(final String id) {
        this.reviewMessageReceiver.receiveReviewToDelete(id);
    }

    private boolean isRatingValid(Integer rating) {
        return rating != null && rating > 0 && rating <= 5;
    }

    private Mono<Review> movieAlreadyRatedByUser(String userId, String movieTitle) {
        return this.reviewRepository.findByUserIdAndMovieTitle(userId, movieTitle);
    }

    private Review mapToReview(User user, NewReviewVO newReviewVO) {
        Review review = new Review();
        review.setUser(user);
        review.setMovieTitle(newReviewVO.getMovieTitle());
        review.setRating(newReviewVO.getRating());
        review.setComment(newReviewVO.getComment());
        return review;
    }

    private Mono<Review> newReviewFromReviewVO(final NewReviewVO newReviewVO) {
        return this.userService.findById(newReviewVO.getUserId())
                .map(user -> mapToReview(user, newReviewVO));
    }

    private Review mapToUpdateReview(User user, UpdateReviewVO updateReviewVO) {
        Review review = new Review();
        review.setId(updateReviewVO.getId());
        review.setUser(user);
        review.setMovieTitle(updateReviewVO.getMovieTitle());
        review.setRating(updateReviewVO.getRating());
        review.setComment(updateReviewVO.getComment());
        return review;
    }
}
