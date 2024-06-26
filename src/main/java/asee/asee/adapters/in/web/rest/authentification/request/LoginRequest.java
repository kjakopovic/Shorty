package asee.asee.adapters.in.web.rest.authentification.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String accountId;

    private String password;
}
