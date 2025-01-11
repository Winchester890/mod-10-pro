package winmovies.com.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import winmovies.com.models.Review;

@Repository
public interface ReviewRepository extends ReactiveCrudRepository<Review, String> {

    Flux<Review> findByUserId(final String userId);
    Mono<Review> findByUserIdAndMovieTitle(final String userId, final String movieTitle);
}
