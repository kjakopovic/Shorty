package asee.asee.adapters.in.web.rest.authentification;

import asee.asee.adapters.in.web.rest.authentification.request.LoginRequest;
import asee.asee.adapters.in.web.rest.authentification.request.RegisterRequest;
import asee.asee.adapters.in.web.rest.authentification.response.LoginResponse;
import asee.asee.adapters.in.web.rest.authentification.response.RegisterResponse;
import asee.asee.application.authentification.service.AuthenticationService;
import asee.asee.application.authentification.service.UserService;
import asee.asee.application.exceptions.ShortyException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentification")
@AllArgsConstructor
public class AuthentificationRestController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        response.setSuccess(false);

        try {
            userService.throwIfUserExists(request.getAccountId());

            response.setPassword(userService.generateRandomPassword());

            var hashedPassword = userService.encryptPassword(response.getPassword());
            userService.validateAndCreateNewUser(request.getAccountId(), hashedPassword);
        }catch (ShortyException e) {
            response.setPassword(null);
            response.setDescription(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request) {
        LoginResponse response = new LoginResponse();
        response.setSuccess(false);

        try {
            userService.checkIsCorrectCredentials(request.getAccountId(), request.getPassword());

            authenticationService.loginUser(request.getAccountId(), request.getPassword());

            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (ShortyException | AuthenticationException e) {
            response.setSuccess(false);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.setSuccess(false);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
