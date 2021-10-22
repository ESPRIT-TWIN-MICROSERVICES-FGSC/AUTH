package esprit.fgsc.auth.controller;


import esprit.fgsc.auth.exception.ResourceNotFoundException;
import esprit.fgsc.auth.model.UserAccount;
import esprit.fgsc.auth.repository.UserRepository;
import esprit.fgsc.auth.security.CurrentUser;
import esprit.fgsc.auth.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
