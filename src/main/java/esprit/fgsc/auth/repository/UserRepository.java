package esprit.fgsc.auth.repository;


import esprit.fgsc.auth.model.UserAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface UserRepository extends ReactiveMongoRepository<UserAccount, String> {
    Flux<UserAccount> findByNameLikeOrderByJoinDateDesc(String name, final Pageable page);
    Mono<UserAccount> findFirstByEmail(String email);
    Mono<UserAccount> findFirstByPasswordResetTokenAndEmail(String passwordResetToken, String email);
    Mono<UserAccount> findFirstByConfirmEmailToken(String emailVerificationToken);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByName(String name);
}
