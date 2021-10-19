package esprit.fgsc.auth.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SignUpRequest {
    @NotNull @NotBlank @Getter @Setter private String name;
    @NotNull @NotBlank @Getter @Setter @Email private String email;
    @NotNull @NotBlank @Getter @Setter private String password;
    @NotNull @NotBlank @Getter @Setter private String returnUrl;
}
