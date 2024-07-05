package asee.shortycore.models.shorty;

import asee.shortycore.models.authentication.UserModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserShortyModel {
    private UserModel user;

    private ShortyModel shorty;

    private int counter = 0;
}
