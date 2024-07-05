package asee.shortyapi;

import asee.shortyapi.shorty.request.ShortyRequest;
import asee.shortyapi.shorty.response.ShortyResponse;
import asee.shortyapplication.shorty.dto.ResolvedHashResponse;
import asee.shortyapplication.shorty.interfaces.IShortyService;
import asee.shortycore.exceptions.ShortyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ShortyApiApplication.class)
@AutoConfigureMockMvc
public class ShortyRestControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private IShortyService shortyService;

    @Test
    public void redirectUserWhenUserNotAuthorizedReturns401Unauthorized() {
        try {
            mvc.perform(get("/123"))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "RandomUser")
    public void redirectUserWhenServiceThrowsAnErrorReturns400BadRequest() {
        try {
            var hash = "123";

            when(shortyService.resolveTheHashedUrl(hash, "RandomUser"))
                    .thenThrow(new ShortyException("There was a mistake in the service!"));

            mvc.perform(get("/123"))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "RandomUser")
    public void redirectUserWhenRedirectionTypeIs301Returns301MovedPermanently() {
        try {
            var hash = "123";
            var url = "https://www.google.com";

            var response = new ResolvedHashResponse(url, 301);

            when(shortyService.resolveTheHashedUrl(hash, "RandomUser"))
                    .thenReturn(response);

            mvc.perform(get("/" + hash))
                    .andExpect(status().isMovedPermanently())
                    .andExpect(header().string("Location", url));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "RandomUser")
    public void redirectUserWhenRedirectionTypeIs302Returns302MovedTemporarily() {
        try {
            var hash = "123";
            var url = "https://www.google.com";

            var response = new ResolvedHashResponse(url, 302);

            when(shortyService.resolveTheHashedUrl(hash, "RandomUser"))
                    .thenReturn(response);

            mvc.perform(get("/" + hash))
                    .andExpect(status().isMovedTemporarily())
                    .andExpect(header().string("Location", url));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "RandomUser")
    public void shortenTheUrlWhenShortyServiceThrowsExceptionReturnsStatus400BadRequest() {
        try {
            var errorMessage = "There was a mistake!";

            var request = new ShortyRequest();

            request.setUrl("https://google.com");
            request.setRedirectType(301);

            var response = new ShortyResponse();

            response.setShortUrl(null);
            response.setDescription(errorMessage);

            when(shortyService.shortenTheUrl(request.getUrl(), request.getRedirectType(), "RandomUser"))
                    .thenThrow(new ShortyException(errorMessage));

            mvc.perform(post("/short")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shortenTheUrlWhenNotAuthenticatedReturnsStatus401Unauthorized() {
        try {
            var request = new ShortyRequest();

            request.setUrl("https://google.com");
            request.setRedirectType(301);

            mvc.perform(post("/short")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "RandomUser")
    public void shortenTheUrlReturnsStatus200Ok() {
        try {
            var hashedUrl = "#wa2!";

            var request = new ShortyRequest();

            request.setUrl("https://google.com");
            request.setRedirectType(301);

            var response = new ShortyResponse();

            response.setShortUrl("http://shorty.com/" + hashedUrl);
            response.setDescription(null);

            when(shortyService.shortenTheUrl(request.getUrl(), request.getRedirectType(), "RandomUser"))
                    .thenReturn(hashedUrl);

            mvc.perform(post("/short")
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
    public void shortenTheUrlWhenNotCorrectRedirectionCodeReturnsStatus400BadRequest() {
        try {
            var request = new ShortyRequest();

            request.setUrl("https://google.com");
            request.setRedirectType(30);

            var response = new ShortyResponse();

            response.setDescription("Please enter redirection type 301 or 302!");

            doThrow(new ShortyException("Please enter redirection type 301 or 302!")).when(shortyService).throwIfIncorrectRedirectionType(any());

            mvc.perform(post("/short")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getUsersStatisticsWhenNotAuthenticatedReturnsStatus401Unauthorized() {
        try {
            var request = new ShortyRequest();

            request.setUrl("https://google.com");
            request.setRedirectType(301);

            mvc.perform(post("/short")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "RandomUser")
    public void getUsersStatisticsReturnsStatus200Ok() {
        try {
            Map<String, Integer> response = new HashMap<>();
            response.put("www.google.com", 0);
            response.put("http://chatgpt.openai", 10);
            response.put("testing/testing", 2);
            response.put("localhost:8000/wow", 25);

            when(shortyService.getUsersShortyStatistics("RandomUser"))
                    .thenReturn(response);

            mvc.perform(get("/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
