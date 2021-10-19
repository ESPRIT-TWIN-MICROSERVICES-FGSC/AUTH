package esprit.fgsc.auth.payload;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class ResetPasswordAttemptRequest {
    @NotBlank @Email @Getter private String email;
    @NotBlank @Getter private String returnUrl;
}
