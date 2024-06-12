package asee.asee.administration.controller;

import asee.asee.PraksaAseeApplication;
import asee.asee.administration.models.UserEntity;
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

    private static final Logger logger = LogManager.getLogger(PraksaAseeApplication.class);
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
        logger.info("Registering user {}", request.toString());

        RegisterResponse response = new RegisterResponse();
        response.setSuccess(false);

        boolean userExists = userService.checkIfUserExists(request.getAccountId());

        if (userExists){
            logger.warn("[REGISTER USER ENDPOINT] - User već postoji");

            response.setDescription("Account ID already exists!");
            return ResponseEntity.badRequest().body(response);
        }

        response.setPassword(userService.generateRandomPassword());
        String hashedPassword = userService.encryptPassword(response.getPassword());

        UserEntity user = new UserEntity();
        user.setAccountId(request.getAccountId());
        user.setPassword(hashedPassword);

        userService.addNewUser(user);

        logger.info("Novi user dodan, uspješna registracija");

        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request) {
        logger.info("Logging user in {}", request.toString());

        LoginResponse response = new LoginResponse();
        response.setSuccess(false);

        try {
            if (userService.isCorrectCredentials(request.getAccountId(), request.getPassword())) {

                authenticationService.loginUser(request.getAccountId(), request.getPassword());

                logger.info("Autentikacija je uspješno provedena!");

                response.setSuccess(true);
                return ResponseEntity.ok(response);
            }

            logger.info("Podaci za autentikaciju su netočni!");

            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {

            logger.error("[LOGIN USER ENDPOINT] - Došlo je do pogreške: {}", e.getMessage());

            response.setSuccess(false);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/short")
    public ResponseEntity<ShortyResponse> shortenTheUrl(@RequestBody ShortyRequest request) {
        logger.info("Starting to shorten URL, request: {}", request.toString());

        ShortyResponse response = new ShortyResponse();

        if (request.getRedirectType() != 301 && request.getRedirectType() != 302) {
            logger.error("[SHORTEN THE URL ENDPOINT] - redirection type mora biti " +
                    "301 ili 302, vi ste unijeli: {}", request.getRedirectType());

            response.setDescription("Molimo vas unesite kod za preusmjeravanje 301 ili 302!");

            return ResponseEntity.badRequest().body(response);
        }

        String loggedInUserAccountId = authenticationService.getLoggedInUsersAccountId();

        try {
            String hashedUrl = shortyService
                    .shortenTheUrl(request.getUrl(), request.getRedirectType(), loggedInUserAccountId);

            response.setShortUrl(HTTP_SHORTY_COM + hashedUrl); //pretpostavka da nam je to domena

            logger.info("Uspješno shortanje URL-a: {}", response.toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("[SHORTEN THE URL ENDPOINT] - došlo je do pogreške: {}", e.getMessage());

            response.setDescription("Došlo je do pogreške!");

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Integer>> getUsersStatistics() {
        logger.info("Ulazak u endpoint za statistiku.");

        String loggedInUserAccountId = authenticationService.getLoggedInUsersAccountId();

        logger.info("Uspješno vraćeni podaci");

        return ResponseEntity.ok(shortyService.getUsersShortyStatistics(loggedInUserAccountId));
    }
}