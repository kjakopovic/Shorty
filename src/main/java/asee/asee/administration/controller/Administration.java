package asee.asee.administration.controller;

import asee.asee.administration.requestDtos.RegisterRequest;
import asee.asee.administration.responseDtos.RegisterResponse;
import asee.asee.administration.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/administration")
public class Administration {

    private UserService userService;

    @Autowired
    public Administration(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> hello(@RequestBody RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        response.setSuccess(false);

        boolean userExists = userService.CheckIfUserExists(request.getAccountId());

        if (!userExists) {
            response.setPassword(userService.generateRandomPassword());

            try {
                userService.addNewUser(request.getAccountId(), response.getPassword());
            }catch (RuntimeException e){
                response.setPassword(null);
                response.setDescription(e.getMessage());
            }

            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } else {
            response.setDescription("Account ID already exists!");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
