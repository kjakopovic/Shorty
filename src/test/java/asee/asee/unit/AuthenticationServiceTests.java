package asee.asee.unit;

import asee.asee.administration.services.AuthenticationService;
import asee.asee.exceptions.ShortyException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationServiceTests {

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    public void loginUserThrowsException(){
        //Arrange
        String accountId = "accountId";
        String password = "password";
        String exceptionMessage = "Bad Credentials";

        when(authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(accountId, password)))
        .thenThrow(new BadCredentialsException(exceptionMessage));

        //Act & Assert
        Assertions.assertThrows(ShortyException.class,
                () -> authenticationService.loginUser(accountId, password),
                exceptionMessage);
    }

    @Test
    public void loginUserLogsTheUserIn(){
        //Arrange
        String accountId = "accountId";
        String password = "password";
        Authentication authentication = new UsernamePasswordAuthenticationToken(accountId, password);

        when(authenticationManager
                .authenticate(authentication))
        .thenReturn(authentication);

        //Act
        try {
            authenticationService.loginUser(accountId, password);
        } catch (ShortyException e) {
            throw new RuntimeException(e);
        }

        String resultAccountId = SecurityContextHolder.getContext().getAuthentication().getName();

        //Assert
        Assertions.assertEquals(accountId, resultAccountId);
    }

    @Test
    @WithMockUser(username = "RandomUser")
    public void getLoggedInUsersAccountIdReturnsCorrectAccountId(){
        //Arrange

        //Act
        String resultAccountId = authenticationService.getLoggedInUsersAccountId();

        //Assert
        Assertions.assertEquals("RandomUser", resultAccountId);
    }
}
