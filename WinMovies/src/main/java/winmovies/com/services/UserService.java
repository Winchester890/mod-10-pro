package winmovies.com.services;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import winmovies.com.models.User;
import winmovies.com.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMessageReceiver userMessageReceiver;

    public UserService(UserRepository userRepository, UserMessageReceiver userMessageReceiver) {
        this.userRepository = userRepository;
        this.userMessageReceiver = userMessageReceiver;
    }

    public Flux<User> findAll() {
        return this.userRepository.findAll();
    }

    public Mono<User> findById(final String id) {
        return this.userRepository.findById(id);
    }

    public void save(final User user) {
        userMessageReceiver.receiveUserToSave(user);
    }

    public void delete(final String id) {
        userMessageReceiver.receiveUserToDelete(id);
    }
}
