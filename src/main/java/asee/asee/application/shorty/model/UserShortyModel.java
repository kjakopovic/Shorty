package asee.asee.application.shorty.model;

import asee.asee.application.authentification.model.UserModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserShortyModel {
    private UserModel user;

    private ShortyModel shorty;

    private int counter = 0;
}
