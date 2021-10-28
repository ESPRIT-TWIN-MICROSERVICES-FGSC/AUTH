package esprit.fgsc.auth.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.Email;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Document(value = "user")
@JsonIgnoreProperties({"password","passwordGenerated","confirmEmailToken","passwordResetToken"})
public class UserAccount extends SecurityProperties.User {
    @MongoId(value = FieldType.OBJECT_ID) @Getter @Setter private String id;
    @Getter @Setter private Instant joinDate = Instant.now();
    @Getter @Setter @Email @Indexed(unique=true) private String email;
    @Getter @Setter private String imageUrl;
    @Getter @Setter private Boolean emailVerified = false;
    @Getter @Setter private AuthProvider provider;
    @Getter @Setter private String providerId;
    @Getter @Setter private String confirmEmailToken = UUID.randomUUID().toString();
    @Getter @Setter private String passwordResetToken;
    @Getter @Setter private Instant passwordResetTime;
    @PersistenceConstructor
    public UserAccount(String name, String password, String email, String imageUrl, AuthProvider provider) {
        super();
        super.setName(name);
        super.setPassword(password);
        getRoles().add(Roles.USER.toString());
        this.email = email;
        this.imageUrl = imageUrl;
        this.provider = provider;
    }
    public UserAccount(){
        super();
        getRoles().add(Roles.USER.toString());
    }

}
