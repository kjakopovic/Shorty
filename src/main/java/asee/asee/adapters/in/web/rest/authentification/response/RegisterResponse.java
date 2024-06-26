package asee.asee.adapters.in.web.rest.authentification.response;

import lombok.Data;

@Data
public class RegisterResponse {
    private boolean success;

    private String password;

    private String description;
}