package asee.asee.administration.requestDtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String accountId;

    private String password;
}
