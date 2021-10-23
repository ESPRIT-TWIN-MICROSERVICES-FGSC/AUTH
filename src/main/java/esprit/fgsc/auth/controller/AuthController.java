package esprit.fgsc.auth.controller;


import esprit.fgsc.auth.model.UserAccount;
import esprit.fgsc.auth.model.AuthProvider;
import esprit.fgsc.auth.payload.*;
import esprit.fgsc.auth.repository.UserRepository;
import esprit.fgsc.auth.security.TokenProvider;
import esprit.fgsc.auth.services.MailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private TokenProvider tokenProvider;
    @Autowired private MailSenderService mailerService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            Optional<UserAccount> optional = userRepository.findFirstByEmail(loginRequest.getEmail()).blockOptional();
            if(!optional.isPresent()) return ResponseEntity.badRequest().body("Account not found");
            UserAccount user = optional.get();
            if(Boolean.FALSE.equals(user.getEmailVerified())) return ResponseEntity.badRequest().body("Please verify your email first");
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok(new AuthResponse(tokenProvider.createToken(authentication)));
        }catch (LockedException e){
            return ResponseEntity.badRequest().body("Account locked by admin");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()).block())) return ResponseEntity.badRequest().body(new ApiResponse(false, "Email already exists"));
        if(Boolean.TRUE.equals(userRepository.existsByName(signUpRequest.getName()).block())) return ResponseEntity.badRequest().body(new ApiResponse(false, "Username already exists"));
        UserAccount user = new UserAccount();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setProvider(AuthProvider.local);
        Mono<UserAccount> result = userRepository.save(user);
        mailerService.sendConfirmEmail(user, signUpRequest.getReturnUrl());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/me").buildAndExpand(result.map(UserAccount::getId)).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "Registered successfully, please check your inbox to verify your email"));
    }
    @PostMapping("/confirm-email")
    public  ResponseEntity<?> validateEmail(@RequestParam String token){
        log.info(token);
        Optional<UserAccount> potential = userRepository.findFirstByConfirmEmailToken(token).blockOptional();
        if(!potential.isPresent()) return ResponseEntity.badRequest().body("Invalid token");
        UserAccount user = potential.get();
        user.setEmailVerified(true);
        userRepository.save(user);
        return ResponseEntity.ok("Email confirmed");
    }
    @PostMapping("/generate-pw-token")
    public ResponseEntity<?> sendResetToken(@Valid @RequestBody ResetPasswordAttemptRequest attempt){
        Optional<UserAccount> possibleUser = this.userRepository.findFirstByEmail(attempt.getEmail()).blockOptional();
        if(!possibleUser.isPresent()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account does not exist");
        UserAccount user = possibleUser.get();
        if(Boolean.FALSE.equals(user.getEmailVerified())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not verified, please verify your email first");
        user.setPasswordResetToken(UUID.randomUUID().toString());
        try{
            mailerService.sendResetToken(user, attempt.getReturnUrl());
        } catch (MailException e){
            return ResponseEntity.internalServerError().body("Failed to send reset token");
        }
        this.userRepository.save(user);
        return ResponseEntity.ok().body("Reset email sent, please check your email");
    }

    @PostMapping("/reset-pw")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest attempt, HttpServletRequest request){
        Optional<UserAccount> potential = userRepository.findFirstByPasswordResetTokenAndEmail(attempt.getResetToken(),attempt.getEmail()).blockOptional();
        if(!potential.isPresent()) return ResponseEntity.badRequest().body("Invalid token / email combination");
        UserAccount user = potential.get();
        if(Boolean.FALSE.equals(user.getEmailVerified())) return ResponseEntity.badRequest().body("Email not verified");
        user.setPassword(passwordEncoder.encode(attempt.getNewPassword()));
        user.setPasswordResetTime(Instant.now());
        user.setPasswordResetToken(null);
        userRepository.save(user);
        mailerService.sendPasswordChangedEmail(user,request.getRemoteAddr());
        return ResponseEntity.ok("Password changed");
    }
    @GetMapping("/user-from-jwt-token")
    public Mono<UserAccount> validateToken(@RequestParam String token){
        if(tokenProvider.validateToken(token)){
            return userRepository.findById(tokenProvider.getUserIdFromToken(token));
        }
        return Mono.empty();
    }
    @PostMapping("/logout")
    public void logout(Authentication authentication){
        authentication.setAuthenticated(false);
    }
}
