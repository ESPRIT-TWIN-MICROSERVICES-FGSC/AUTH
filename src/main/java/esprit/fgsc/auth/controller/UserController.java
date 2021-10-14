package esprit.fgsc.auth.controller;


import esprit.fgsc.auth.exception.ResourceNotFoundException;
import esprit.fgsc.auth.model.User;
import esprit.fgsc.auth.repository.UserRepository;
import esprit.fgsc.auth.security.CurrentUser;
import esprit.fgsc.auth.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@FeignClient(name = "userinfo")
@RibbonClient(name = "userinfo")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired private UserRepository userRepository;

    @GetMapping("/user/me")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}
