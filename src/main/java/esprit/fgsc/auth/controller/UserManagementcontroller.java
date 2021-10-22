package esprit.fgsc.auth.controller;

import esprit.fgsc.auth.model.UserAccount;
import esprit.fgsc.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@FeignClient(name = "users")
@RibbonClient(name = "users")
public class UserManagementcontroller {
    private final UserRepository usersRepository;
    @Autowired
    public UserManagementcontroller(UserRepository usersRepository){
        this.usersRepository = usersRepository;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserAccount> getById(@RequestParam String id){return usersRepository.findById(id);}
    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Long> count() {return usersRepository.count();}
    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    public Flux<UserAccount> paginated(final @RequestParam int page, final @RequestParam int size, final @RequestParam(required = false, defaultValue = "") String name) {
        return usersRepository.findByNameLikeOrderByJoinDateDesc(name, PageRequest.of(page, size));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserAccount> add(@Validated @RequestBody UserAccount user){return usersRepository.insert(user);}
    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<UserAccount> update(@Validated @RequestBody UserAccount user){return usersRepository.save(user);}
    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> delete(@RequestParam String userId){return usersRepository.deleteById(userId);}
}

//    private final ExternalServices externalServices = new ExternalServices();
//    @GetMapping("/external")
//    @ResponseStatus(HttpStatus.OK)
//    public Object TestExternalRestTemplate() throws URISyntaxException {
//        return externalServices.TestExternalGet();
//    }
//    @GetMapping("/external/reactive")
//    @ResponseStatus(HttpStatus.OK)
//    public Object TestWebClientReactive() throws URISyntaxException {
//        return externalServices.TestReactiveExternalGet();
//    }
//    @GetMapping("/internal")
//    @ResponseStatus(HttpStatus.OK)
//    public Object TestInternal() {
//        throw new NotImplementedException();
//    }
//    @GetMapping("/internal/reactive")
//    @ResponseStatus(HttpStatus.OK)
//    public Object TestInternalReactive() {
//        throw new NotImplementedException();
//    }
//    private static HttpEntity<?> getHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
//        return new HttpEntity<>(headers);
//    }
// .delayElements(Duration.ofMillis(3000))