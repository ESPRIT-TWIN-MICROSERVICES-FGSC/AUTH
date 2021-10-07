package esprit.fgsc.auth.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Document
public class User extends SecurityProperties.User {
    @MongoId @Getter @Setter private String id;
    @Getter @Setter private Date joinDate = new Date();
    @Getter @Setter @Email @Indexed(unique=true) private String email;
    @Getter @Setter private String imageUrl;
    @Getter @Setter private Boolean emailVerified = false;
    // @Enumerated(EnumType.STRING)
    @NotNull @Getter @Setter private AuthProvider provider;
    @Getter @Setter private String providerId;
}
