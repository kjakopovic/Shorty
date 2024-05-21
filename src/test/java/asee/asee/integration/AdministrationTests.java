package asee.asee.integration;

import asee.asee.PraksaAseeApplication;
import asee.asee.administration.repositories.IUserRepository;
import asee.asee.administration.requestDtos.RegisterRequest;
import asee.asee.administration.responseDtos.RegisterResponse;
import asee.asee.administration.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = PraksaAseeApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AdministrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private IUserRepository userRepository;

    @Test
    @Rollback
    public void registerUser_when_accountIdAlreadyExists_returns_status400BadRequest() {
        try {
            RegisterRequest request = new RegisterRequest();
            RegisterResponse response = new RegisterResponse();

            request.setAccountId("Karlo");

            response.setSuccess(false);
            response.setDescription("Account ID already exists!");
            response.setPassword(null);

            when(userRepository.existsById(request.getAccountId())).thenReturn(true);

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
    @Rollback
    public void registerUser_returns_status200Ok() {
        try {
            RegisterRequest request = new RegisterRequest();
            request.setAccountId("TestingId");

            RegisterResponse response = new RegisterResponse();
            response.setSuccess(true);
            response.setDescription(null);

            String responseJson = mapper.writeValueAsString(response);

            when(userRepository.existsById(request.getAccountId())).thenReturn(false);

            mvc.perform(post("/administration/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.description").isEmpty());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
