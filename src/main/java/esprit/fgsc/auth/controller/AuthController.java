package esprit.fgsc.auth.controller;


import esprit.fgsc.auth.exception.BadRequestException;
import esprit.fgsc.auth.model.User;
import esprit.fgsc.auth.model.AuthProvider;
import esprit.fgsc.auth.payload.ApiResponse;
import esprit.fgsc.auth.payload.AuthResponse;
import esprit.fgsc.auth.payload.LoginRequest;
import esprit.fgsc.auth.payload.SignUpRequest;
import esprit.fgsc.auth.repository.UserRepository;
import esprit.fgsc.auth.security.TokenProvider;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/auth")
@FeignClient(name = "auth")
@RibbonClient(name = "auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private TokenProvider tokenProvider;
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok(new AuthResponse(tokenProvider.createToken(authentication)));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) throw new BadRequestException("Email address already in use.");
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
//        user.setPassword(signUpRequest.getPassword());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setProvider(AuthProvider.local);
        User result = userRepository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/me").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
    @GetMapping("/validate-token")
    public String validateToken(@RequestParam String token){
        if(tokenProvider.validateToken(token)){
            return tokenProvider.getUserIdFromToken(token);
        }
        return null;
    }

}
