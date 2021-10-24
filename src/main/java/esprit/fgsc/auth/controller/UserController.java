package esprit.fgsc.auth.controller;

import esprit.fgsc.auth.exception.ResourceNotFoundException;
import esprit.fgsc.auth.model.UserAccount;
import esprit.fgsc.auth.repository.UserRepository;
import esprit.fgsc.auth.security.CurrentUser;
import esprit.fgsc.auth.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class UserController {
    private final UserRepository userRepository;
    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Autowired
    private WebClient reactiveWebClient;
    @GetMapping("/test")
    public Flux<UserAccount> serviceUrl(@RequestHeader("Authorization") String token) {
        return reactiveWebClient.get()
                .uri("https://fgsc-gateway.herokuapp.com/api/auth/paginated")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .retrieve().bodyToFlux(UserAccount.class);
    }
}
