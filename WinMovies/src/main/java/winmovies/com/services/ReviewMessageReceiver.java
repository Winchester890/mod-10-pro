package winmovies.com.services;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import winmovies.com.models.Review;
import winmovies.com.repositories.ReviewRepository;

@Component
public class ReviewMessageReceiver {

    private final ReviewRepository reviewRepository;

    public ReviewMessageReceiver(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @JmsListener(destination = "reviews", containerFactory = "myFactory")
    public void receiveReviewToSave(final Review review) {
        this.reviewRepository.save(review)
                .doOnSuccess(result -> {
            System.out.println("Review saved succesfully: " + review.toString());
        })
                .doOnError(error -> {
                    System.out.println("Error saving the review: " + error.getMessage());
                })
                .subscribe();

    }

    @JmsListener(destination = "reviews", containerFactory = "myFactory")
    public void receiveReviewToDelete(final String id) {
        this.reviewRepository.deleteById(id)
                .doOnSuccess(result -> {
                    System.out.println("Review deleted successfully: " + id);
                })
                .doOnError(error -> {
                    System.out.println("Error deleting review with ID: " + id + " - " + error.getMessage());
                })
                .subscribe();
    }



}
