package asee.shortycore.models.authentication;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserModel {
    @Length(max = 50, message = "Account ID must be between 1 and 50 characters long!")
    private String accountId;

    private String password;
}