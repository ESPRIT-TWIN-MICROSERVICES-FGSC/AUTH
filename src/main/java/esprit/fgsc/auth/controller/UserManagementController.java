package esprit.fgsc.auth.controller;

import esprit.fgsc.auth.exception.ResourceNotFoundException;
import esprit.fgsc.auth.model.UserAccount;
import esprit.fgsc.auth.repository.UserRepository;
import esprit.fgsc.auth.security.CurrentUser;
import esprit.fgsc.auth.security.UserPrincipal;
import esprit.fgsc.auth.services.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@FeignClient(name = "users")
@RibbonClient(name = "users")
@RequestMapping("/users")
//@PreAuthorize(value="hasRole('ROLE_ADMIN')")
public class UserManagementController {
    private final UserRepository usersRepository;
    private final MailSenderService mailSenderService;
    @Autowired
    public UserManagementController(UserRepository usersRepository, MailSenderService mailSenderService){
        this.usersRepository = usersRepository;
        this.mailSenderService = mailSenderService;
    }
    @GetMapping
    public Mono<UserAccount> getById(@RequestParam String id){return usersRepository.findById(id);}
    @GetMapping("/count")
    public Mono<Long> count() {return usersRepository.count();}
    @GetMapping("/paginated")
    public Flux<UserAccount> paginated(final @RequestParam(required = false, defaultValue = "0") int page, final @RequestParam(required = false, defaultValue = "10") int size, final @RequestParam(required = false, defaultValue = "") String name) {
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
    @GetMapping("/send-mail")
    public Mono<?> sendEmail(@CurrentUser UserPrincipal userPrincipal, @RequestParam String email, @RequestParam String subject, @RequestParam String text){
        return this.usersRepository.findFirstByEmail(email).map(userAccount -> {
            this.mailSenderService.sendEmailAsync(email,subject,text);
            return Mono.just("Email sent asynchronously, so no way of verifying if its received or not");
        }).switchIfEmpty(Mono.error(() -> new ResourceNotFoundException("User","Email",email)));
    }
}