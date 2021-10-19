package esprit.fgsc.auth.repository;


import esprit.fgsc.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPasswordResetTokenAndEmail(String passwordResetToken, String email);
    Optional<User> findByConfirmEmailToken(String emailVerificationToken);
    Boolean existsByEmail(String email);
    Boolean existsByName(String name);
}
