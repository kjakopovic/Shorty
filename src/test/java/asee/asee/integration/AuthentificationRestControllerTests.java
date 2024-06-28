package asee.asee.integration;

import asee.asee.PraksaAseeApplication;
import asee.asee.adapters.in.web.rest.authentification.request.RegisterRequest;
import asee.asee.adapters.in.web.rest.authentification.response.RegisterResponse;
import asee.asee.adapters.in.web.rest.authentification.request.LoginRequest;
import asee.asee.adapters.in.web.rest.authentification.response.LoginResponse;
import asee.asee.application.authentification.service.UserService;
import asee.asee.application.exceptions.ShortyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = PraksaAseeApplication.class)
@AutoConfigureMockMvc
public class AuthentificationRestControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    public void registerUserWhenAccountIdAlreadyExistsReturnsStatus400BadRequest() {
        try {
            var request = new RegisterRequest();
            var response = new RegisterResponse();

            request.setAccountId("Karlo");

            response.setSuccess(false);
            response.setDescription("Account ID already exists!");
            response.setPassword(null);

            doThrow(new ShortyException("Account ID already exists!")).when(userService).throwIfUserExists(any());

            mvc.perform(post("/authentification/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void registerUserReturnsStatus200Ok() {
        try {
            var request = new RegisterRequest();
            request.setAccountId("TestingId");

            var response = new RegisterResponse();
            response.setSuccess(true);
            response.setDescription(null);
            response.setPassword("Generated");

            when(userService.generateRandomPassword()).thenReturn("Generated");
            when(userService.encryptPassword("Generated")).thenReturn("ratedGene");

            mvc.perform(post("/authentification/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void loginUserWhenIncorrectCredentialsReturnsStatus400BadRequest() {
        try {
            var request = new LoginRequest();
            var response = new LoginResponse();

            request.setAccountId("Karlo");
            request.setPassword("Password1_!");

            response.setSuccess(false);

            doThrow(new ShortyException()).when(userService).checkIsCorrectCredentials(any(), any());

            mvc.perform(post("/authentification/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void loginUserWhenCorrectCredentialsAndExceptionThrownReturnsStatus400BadRequest() {
        try {
            LoginRequest request = new LoginRequest();
            LoginResponse response = new LoginResponse();

            request.setAccountId("Karlo");
            request.setPassword("Password1_!");

            response.setSuccess(false);

            when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getAccountId(), request.getPassword())))
                    .thenThrow(new BadCredentialsException("Exception"));

            mvc.perform(post("/authentification/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void loginUserWhenCorrectCredentialsAndNoErrorReturnsStatus200Ok() {
        try {
            var request = new LoginRequest();
            var response = new LoginResponse();

            request.setAccountId("Karlo");
            request.setPassword("Password1_!");

            response.setSuccess(true);

            when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getAccountId(), request.getPassword())))
                    .thenReturn(new UsernamePasswordAuthenticationToken(request.getAccountId(), request.getPassword()));

            mvc.perform(post("/authentification/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
