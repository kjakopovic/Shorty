package asee.asee.unit;

import asee.asee.administration.models.UserEntity;
import asee.asee.administration.repositories.IUserRepository;
import asee.asee.administration.services.UserService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @MockBean
    private IUserRepository userRepository;

    @Test
    public void isCorrectCredentials_whenUserIsNotFound_ReturnsFalse(){
        //Arrange
        String accountId = "Karlo";
        String exceptionMessage = "User not found";

        when(userRepository.findById(accountId)).thenThrow(new NoSuchElementException(exceptionMessage));

        //Act
        boolean result = userService.isCorrectCredentials(accountId, "SomeFakePassword");

        //Assert
        Assertions.assertFalse(result);
    }

    @Test
    public void isCorrectCredentials_whenCorrectPassword_And_correctAccountId_IsEntered_ReturnsTrue(){

        //Arrange
        String accountId = "Karlo";
        String password = "ojgisfsd";

        UserEntity user = new UserEntity();
        user.setAccountId(accountId);
        user.setPassword(userService.encryptPassword(password));

        when(userRepository.findById(accountId)).thenReturn(Optional.of(user));

        //Act
        boolean result = userService.isCorrectCredentials(accountId, password);

        //Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void isCorrectCredentials_whenIncorrectPassword_IsEntered_ReturnsFalse(){

        //Arrange
        String accountId = "Karlo";
        String password = "ojgisfsd";

        UserEntity user = new UserEntity();
        user.setAccountId(accountId);
        user.setPassword(userService.encryptPassword(password));

        when(userRepository.findById(accountId)).thenReturn(Optional.of(user));

        //Act
        boolean result = userService.isCorrectCredentials(accountId, "SomeFakePassword");

        //Assert
        Assertions.assertFalse(result);
    }

    @Test
    public void isCorrectCredentials_whenIncorrectAccountId_IsEntered_ReturnsFalse(){

        //Arrange
        String accountId = "Karlo";
        String password = "ojgisfsd";

        UserEntity user = new UserEntity();
        user.setAccountId(accountId);
        user.setPassword(userService.encryptPassword(password));

        when(userRepository.findById(accountId)).thenReturn(Optional.of(user));

        //Act
        boolean result = userService.isCorrectCredentials("FakeId", password);

        //Assert
        Assertions.assertFalse(result);
    }
}
