package asee.asee.integration;

import asee.asee.PraksaAseeApplication;
import asee.asee.administration.responseDtos.ResolvedHashResponse;
import asee.asee.administration.services.ShortyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = PraksaAseeApplication.class)
@AutoConfigureMockMvc
public class RedirectionTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ShortyService shortyService;

    @Test
    public void redirectUser_when_userNotAuthorized_returns_401Unauthorized() {
        try {
            mvc.perform(get("/123"))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "RandomUser")
    public void redirectUser_when_serviceThrowsAnError_returns_404NotFound() {
        try {
            String hash = "123";

            when(shortyService.resolveTheHashedUrl(hash, "RandomUser"))
                    .thenThrow(new NoSuchElementException("There was a mistake in the service!"));

            mvc.perform(get("/123"))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "RandomUser")
    public void redirectUser_when_redirectionTypeIs301_returns_301MovedPermanently() {
        try {
            String hash = "123";
            String url = "https://www.google.com";

            ResolvedHashResponse response = new ResolvedHashResponse(url, 301);

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
    public void redirectUser_when_redirectionTypeIs302_returns_302MovedTemporarily() {
        try {
            String hash = "123";
            String url = "https://www.google.com";

            ResolvedHashResponse response = new ResolvedHashResponse(url, 302);

            when(shortyService.resolveTheHashedUrl(hash, "RandomUser"))
                    .thenReturn(response);

            mvc.perform(get("/" + hash))
                    .andExpect(status().isMovedTemporarily())
                    .andExpect(header().string("Location", url));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
