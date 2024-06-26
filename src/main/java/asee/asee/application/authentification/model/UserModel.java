package asee.asee.application.authentification.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserModel {
    @Length(max = 50, message = "Account ID mora biti maksimalno 50 charactera!")
    private String accountId;

    private String password;
}