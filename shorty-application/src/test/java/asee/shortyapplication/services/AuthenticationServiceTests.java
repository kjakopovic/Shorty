package asee.shortyapplication.services;

import asee.shortyapplication.ShortyApplication;
import asee.shortyapplication.authentication.interfaces.IAuthenticationService;
import asee.shortycore.exceptions.ShortyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShortyApplication.class)
public class AuthenticationServiceTests {

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private IAuthenticationService authenticationService;

    @Test
    public void loginUserThrowsException(){
        //Arrange
        var accountId = "accountId";
        var password = "password";
        var exceptionMessage = "Bad Credentials";

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
        var accountId = "accountId";
        var password = "password";
        var authentication = new UsernamePasswordAuthenticationToken(accountId, password);

        when(authenticationManager
                .authenticate(authentication))
        .thenReturn(authentication);

        //Act
        try {
            authenticationService.loginUser(accountId, password);
        } catch (ShortyException e) {
            throw new RuntimeException(e);
        }

        var resultAccountId = SecurityContextHolder.getContext().getAuthentication().getName();

        //Assert
        Assertions.assertEquals(accountId, resultAccountId);
    }

    @Test
    @WithMockUser(username = "RandomUser")
    public void getLoggedInUsersAccountIdReturnsCorrectAccountId(){
        //Arrange

        //Act
        var resultAccountId = authenticationService.getLoggedInUsersAccountId();

        //Assert
        Assertions.assertEquals("RandomUser", resultAccountId);
    }
}
