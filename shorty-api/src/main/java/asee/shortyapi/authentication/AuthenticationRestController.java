package asee.shortyapi.authentication;

import asee.shortyapi.authentication.request.LoginRequest;
import asee.shortyapi.authentication.request.RegisterRequest;
import asee.shortyapi.authentication.response.LoginResponse;
import asee.shortyapi.authentication.response.RegisterResponse;
import asee.shortyapplication.authentication.interfaces.IAuthenticationService;
import asee.shortyapplication.authentication.interfaces.IUserService;
import asee.shortycore.exceptions.ShortyException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
@AllArgsConstructor
public class AuthenticationRestController {
    private final IUserService userService;
    private final IAuthenticationService authenticationService;

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
