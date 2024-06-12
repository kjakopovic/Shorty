package asee.asee.administration.controller;

import asee.asee.PraksaAseeApplication;
import asee.asee.exceptions.ShortyException;
import asee.asee.administration.requestDtos.LoginRequest;
import asee.asee.administration.requestDtos.RegisterRequest;
import asee.asee.administration.requestDtos.ShortyRequest;
import asee.asee.administration.responseDtos.LoginResponse;
import asee.asee.administration.responseDtos.RegisterResponse;
import asee.asee.administration.responseDtos.ShortyResponse;
import asee.asee.administration.services.AuthenticationService;
import asee.asee.administration.services.ShortyService;
import asee.asee.administration.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/administration")
public class Administration {

    public static final String HTTP_SHORTY_COM = "http://shorty.com/";
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final ShortyService shortyService;

    @Autowired
    public Administration(UserService userService, AuthenticationService authenticationService, ShortyService shortyService) {
        this.userService = userService;
        this.shortyService = shortyService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        response.setSuccess(false);

        boolean userExists = userService.checkIfUserExists(request.getAccountId());

        if (userExists){
            response.setDescription("Account ID already exists!");
            return ResponseEntity.badRequest().body(response);
        }

        response.setPassword(userService.generateRandomPassword());

        try {
            String hashedPassword = userService.encryptPassword(response.getPassword());
            userService.validateAndCreateNewUser(request.getAccountId(), hashedPassword);
        }
        catch (ShortyException e) {
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
            if (userService.isCorrectCredentials(request.getAccountId(), request.getPassword())) {

                authenticationService.loginUser(request.getAccountId(), request.getPassword());

                response.setSuccess(true);
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.setSuccess(false);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/short")
    public ResponseEntity<ShortyResponse> shortenTheUrl(@RequestBody ShortyRequest request) {
        ShortyResponse response = new ShortyResponse();

        if (request.getRedirectType() != 301 && request.getRedirectType() != 302) {
            response.setDescription("Molimo vas unesite kod za preusmjeravanje 301 ili 302!");

            return ResponseEntity.badRequest().body(response);
        }

        try {
            String hashedUrl = shortyService.shortenTheUrl(
                    request.getUrl(),
                    request.getRedirectType(),
                    authenticationService.getLoggedInUsersAccountId());

            response.setShortUrl(HTTP_SHORTY_COM + hashedUrl); //pretpostavka da nam je to domena

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setDescription("Došlo je do pogreške: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Integer>> getUsersStatistics() {
        String loggedInUserAccountId = authenticationService.getLoggedInUsersAccountId();

        return ResponseEntity.ok(shortyService.getUsersShortyStatistics(loggedInUserAccountId));
    }
}