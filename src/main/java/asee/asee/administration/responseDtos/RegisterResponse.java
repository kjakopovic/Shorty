package asee.asee.administration.responseDtos;

import lombok.Data;

@Data
public class RegisterResponse {
    private boolean success;

    private String password;

    private String description;
}