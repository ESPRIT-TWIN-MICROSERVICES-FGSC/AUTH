package esprit.fgsc.auth.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Document
@JsonIgnoreProperties({"password","passwordGenerated"})
public class User extends SecurityProperties.User {
    @Getter @Setter private String id;
    @Getter @Setter private Date joinDate = new Date();
    @Getter @Setter @Email @Indexed(unique=true) private String email;
    @Getter @Setter private String imageUrl;
    @Getter @Setter private Boolean emailVerified = false;
    @NotNull @Getter @Setter private AuthProvider provider;
    @Getter @Setter private String providerId;
    public User(){
        getRoles().add(Roles.USER.toString());
    }
}
