package esprit.fgsc.auth.controller;

import esprit.fgsc.auth.exception.ResourceNotFoundException;
import esprit.fgsc.auth.model.UserAccount;
import esprit.fgsc.auth.repository.UserRepository;
import esprit.fgsc.auth.security.CurrentUser;
import esprit.fgsc.auth.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired private UserRepository userRepository;

    @GetMapping("/me")
    public UserAccount getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        log.info(userPrincipal.getId());
        return userRepository.findById(userPrincipal.getId()).blockOptional()
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

    @Autowired
    private WebClient restTemplate;
    @GetMapping("/test")
    public Flux<UserAccount> serviceUrl(@RequestHeader("Authorization") String token) {
        return restTemplate.get()
                .uri("https://fgsc-gateway.herokuapp.com/api/auth/paginated")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .retrieve().bodyToFlux(UserAccount.class);
    }
}
