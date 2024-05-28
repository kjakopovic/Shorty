package asee.asee.administration.controller;

import asee.asee.administration.models.UserEntity;
import asee.asee.administration.requestDtos.LoginRequest;
import asee.asee.administration.requestDtos.RegisterRequest;
import asee.asee.administration.requestDtos.ShortyRequest;
import asee.asee.administration.responseDtos.LoginResponse;
import asee.asee.administration.responseDtos.RegisterResponse;
import asee.asee.administration.responseDtos.ShortyResponse;
import asee.asee.administration.services.ShortyService;
import asee.asee.administration.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/administration")
public class Administration {

    public static final String HTTP_SHORTY_COM = "http://shorty.com/";
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final ShortyService shortyService;

    @Autowired
    public Administration(UserService userService, AuthenticationManager authenticationManager, ShortyService shortyService) {
        this.userService = userService;
        this.shortyService = shortyService;
        this.authenticationManager = authenticationManager;
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
        String hashedPassword = userService.encryptPassword(response.getPassword());

        UserEntity user = new UserEntity();
        user.setAccountId(request.getAccountId());
        user.setPassword(hashedPassword);

        userService.addNewUser(user);

        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request) {
        LoginResponse response = new LoginResponse();
        response.setSuccess(false);

        try {
            if (userService.isCorrectCredentials(request.getAccountId(), request.getPassword())) {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getAccountId(), request.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            String hashedUrl = shortyService
                    .shortenTheUrl(request.getUrl(), request.getRedirectType(), authentication.getName());

            response.setShortUrl(HTTP_SHORTY_COM + hashedUrl); //pretpostavka da nam je to domena

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setDescription("Došlo je do pogreške: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}