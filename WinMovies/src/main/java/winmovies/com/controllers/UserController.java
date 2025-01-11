package winmovies.com.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import winmovies.com.models.User;
import winmovies.com.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JmsTemplate jmsTemplate;

    public UserController(UserService userService, JmsTemplate jmsTemplate) {
        this.userService = userService;
        this.jmsTemplate = jmsTemplate;
    }

    @GetMapping
    public ResponseEntity<Flux<User>> findAll() {
        Flux<User> users = this.userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<User>> findById(@PathVariable final String id) {
        Mono<User> user = this.userService.findById(id);
        return user != null ?
                ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity save(@RequestBody final User user) {
        this.userService.save(user);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody final User user) {
        this.userService.save(user);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable final String id) {
        this.userService.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
