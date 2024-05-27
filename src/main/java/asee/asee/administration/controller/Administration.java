package asee.asee.administration.controller;

import asee.asee.administration.models.UserEntity;
import asee.asee.administration.requestDtos.LoginRequest;
import asee.asee.administration.requestDtos.RegisterRequest;
import asee.asee.administration.responseDtos.LoginResponse;
import asee.asee.administration.responseDtos.RegisterResponse;
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

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public Administration(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        response.setSuccess(false);

        boolean userExists = userService.CheckIfUserExists(request.getAccountId());

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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request){
        LoginResponse response = new LoginResponse();
        response.setSuccess(false);

        try{
            if(userService.isCorrectCredentials(request.getAccountId(), request.getPassword())){
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getAccountId(), request.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                response.setSuccess(true);
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.badRequest().body(response);
        }catch (Exception e){
            response.setSuccess(false);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
