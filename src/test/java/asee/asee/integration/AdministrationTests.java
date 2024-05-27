package asee.asee.integration;

import asee.asee.PraksaAseeApplication;
import asee.asee.administration.models.UserEntity;
import asee.asee.administration.requestDtos.LoginRequest;
import asee.asee.administration.requestDtos.RegisterRequest;
import asee.asee.administration.requestDtos.ShortyRequest;
import asee.asee.administration.responseDtos.LoginResponse;
import asee.asee.administration.responseDtos.RegisterResponse;
import asee.asee.administration.responseDtos.ShortyResponse;
import asee.asee.administration.services.ShortyService;
import asee.asee.administration.services.UserService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = PraksaAseeApplication.class)
@AutoConfigureMockMvc
public class AdministrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @MockBean
    private ShortyService shortyService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    public void registerUser_when_accountIdAlreadyExists_returns_status400BadRequest() {
        try {
            RegisterRequest request = new RegisterRequest();
            RegisterResponse response = new RegisterResponse();

            request.setAccountId("Karlo");

            response.setSuccess(false);
            response.setDescription("Account ID already exists!");
            response.setPassword(null);

            when(userService.checkIfUserExists(request.getAccountId())).thenReturn(true);

            mvc.perform(post("/administration/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void registerUser_returns_status200Ok() {
        try {
            RegisterRequest request = new RegisterRequest();
            request.setAccountId("TestingId");

            RegisterResponse response = new RegisterResponse();
            response.setSuccess(true);
            response.setDescription(null);
            response.setPassword("Generated");

            when(userService.checkIfUserExists(request.getAccountId())).thenReturn(false);
            when(userService.generateRandomPassword()).thenReturn("Generated");
            when(userService.encryptPassword("Generated")).thenReturn("ratedGene");

            mvc.perform(post("/administration/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void loginUser_when_inCorrectCredentials_returns_status400BadRequest() {
        try {
            LoginRequest request = new LoginRequest();
            LoginResponse response = new LoginResponse();

            request.setAccountId("Karlo");
            request.setPassword("Password1_!");

            response.setSuccess(false);

            UserEntity user = new UserEntity();
            user.setAccountId(request.getAccountId());
            user.setPassword("Encoded_password");

            when(userService.isCorrectCredentials(request.getAccountId(), request.getPassword())).thenReturn(false);

            mvc.perform(post("/administration/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void loginUser_when_correctCredentials_and_ExceptionThrown_returns_status400BadRequest() {
        try {
            LoginRequest request = new LoginRequest();
            LoginResponse response = new LoginResponse();

            request.setAccountId("Karlo");
            request.setPassword("Password1_!");

            response.setSuccess(false);

            UserEntity user = new UserEntity();
            user.setAccountId(request.getAccountId());
            user.setPassword("Encoded_password");

            when(userService.isCorrectCredentials(request.getAccountId(), request.getPassword())).thenReturn(true);
            when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getAccountId(), request.getPassword())))
                    .thenThrow(new BadCredentialsException("Exception"));

            mvc.perform(post("/administration/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void loginUser_when_correctCredentials_andNoError_returns_status200Ok() {
        try {
            LoginRequest request = new LoginRequest();
            LoginResponse response = new LoginResponse();

            request.setAccountId("Karlo");
            request.setPassword("Password1_!");

            response.setSuccess(true);

            UserEntity user = new UserEntity();
            user.setAccountId(request.getAccountId());
            user.setPassword("Encoded_password");

            when(userService.isCorrectCredentials(request.getAccountId(), request.getPassword())).thenReturn(true);
            when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getAccountId(), request.getPassword())))
                    .thenReturn(new UsernamePasswordAuthenticationToken(request.getAccountId(), request.getPassword()));

            mvc.perform(post("/administration/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "RandomUser")
    public void shortenTheUrl_when_shortyService_throwsException_returns_status400BadRequest() {
        try {
            String errorMessage = "There was a mistake!";

            ShortyRequest request = new ShortyRequest();

            request.setUrl("https://google.com");
            request.setRedirectType(301);

            ShortyResponse response = new ShortyResponse();

            response.setShortUrl(null);
            response.setDescription("Došlo je do pogreške: " + errorMessage);

            when(shortyService.shortenTheUrl(request.getUrl(), request.getRedirectType(), "RandomUser"))
                    .thenThrow(new NoSuchElementException(errorMessage));

            mvc.perform(post("/administration/short")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shortenTheUrl_when_notAuthenticated_returns_status401Unauthorized() {
        try {
            String errorMessage = "There was a mistake!";

            ShortyRequest request = new ShortyRequest();

            request.setUrl("https://google.com");
            request.setRedirectType(301);

            ShortyResponse response = new ShortyResponse();

            response.setShortUrl(null);
            response.setDescription("Došlo je do pogreške: " + errorMessage);

            when(shortyService.shortenTheUrl(request.getUrl(), request.getRedirectType(), "RandomUser"))
                    .thenThrow(new NoSuchElementException(errorMessage));

            mvc.perform(post("/administration/short")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "RandomUser")
    public void shortenTheUrl_returns_status200Ok() {
        try {
            String hashedUrl = "#wa2!";

            ShortyRequest request = new ShortyRequest();

            request.setUrl("https://google.com");
            request.setRedirectType(301);

            ShortyResponse response = new ShortyResponse();

            response.setShortUrl("http://shorty.com/" + hashedUrl);
            response.setDescription(null);

            when(shortyService.shortenTheUrl(request.getUrl(), request.getRedirectType(), "RandomUser"))
                    .thenReturn(hashedUrl);

            mvc.perform(post("/administration/short")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
