package esprit.fgsc.auth.payload;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.UUID;

public class ResetPasswordRequest extends ResetPasswordAttemptRequest{
    @NotBlank @NotEmpty @Getter private String newPassword;
    @NotBlank @NotEmpty @Getter private String resetToken;
}
