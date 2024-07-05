package asee.shortyapi.authentication.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {
    private boolean success;

    private String password;

    private String description;
}