package asee.asee.administration.controller;

import asee.asee.administration.models.UserEntity;
import asee.asee.administration.requestDtos.RegisterRequest;
import asee.asee.administration.requestDtos.ShortyRequest;
import asee.asee.administration.responseDtos.RegisterResponse;
import asee.asee.administration.responseDtos.ShortyResponse;
import asee.asee.administration.services.ShortyService;
import asee.asee.administration.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/administration")
public class Administration {

    private final UserService userService;
    private final ShortyService shortyService;

    @Autowired
    public Administration(UserService userService, ShortyService shortyService) {
        this.userService = userService;
        this.shortyService = shortyService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> hello(@RequestBody RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        response.setSuccess(false);

        boolean userExists = userService.checkIfUserExists(request.getAccountId());

        if (!userExists) {
            response.setPassword(userService.generateRandomPassword());
            String hashedPassword = userService.encryptPassword(response.getPassword());

            UserEntity user = new UserEntity();
            user.setAccountId(request.getAccountId());
            user.setPassword(hashedPassword);

            userService.addNewUser(user);

            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } else {
            response.setDescription("Account ID already exists!");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/short")
    public ResponseEntity<ShortyResponse> shortenTheUrl(@RequestBody ShortyRequest request) {
        ShortyResponse response = new ShortyResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            String hashedUrl = shortyService
                    .shortenTheUrl(request.getUrl(), request.getRedirectType(), authentication.getName());

            response.setShortUrl("http://shorty.com/" + hashedUrl); //pretpostavka da nam je to domena

            return ResponseEntity.ok(response);
        }catch (Exception e) {
            response.setDescription(e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}
