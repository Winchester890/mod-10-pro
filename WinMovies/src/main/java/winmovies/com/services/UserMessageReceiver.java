package winmovies.com.services;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import winmovies.com.models.User;
import winmovies.com.repositories.UserRepository;

@Component
public class UserMessageReceiver {

    private final UserRepository userRepository;

    public UserMessageReceiver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @JmsListener(destination = "users", containerFactory = "myFactory")
    public void receiveUserToSave(final User user) {
        this.userRepository.save(user)
                .doOnSuccess(result -> {
                    System.out.println("User saved successfully: " + user.toString());
                })
                .doOnError(error -> {
                    System.out.println("Error saving the user: " + error.getMessage());
                })
                .subscribe();
    }

    @JmsListener(destination = "users", containerFactory = "myFactory")
    public void receiveUserToDelete(final String id) {
        this.userRepository.deleteById(id)
                .doOnSuccess(result -> {
                    System.out.println("User deleted successfully: " + id);
                })
                .doOnError(error -> {
                    System.out.println("Error deleting user with ID: " + id + " - " + error.getMessage());
                })
                .subscribe();
    }
}
