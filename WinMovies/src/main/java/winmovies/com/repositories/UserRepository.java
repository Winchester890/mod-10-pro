package winmovies.com.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import winmovies.com.models.User;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {

}
