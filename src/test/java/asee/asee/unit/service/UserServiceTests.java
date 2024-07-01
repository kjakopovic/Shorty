package asee.asee.unit.service;

import asee.asee.application.authentification.dao.IUserDAO;
import asee.asee.application.authentification.model.UserModel;
import asee.asee.application.authentification.service.UserService;
import asee.asee.application.exceptions.ShortyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @MockBean
    private IUserDAO userDAO;

    @Test
    public void checkIsCorrectCredentialsWhenUserIsNotFoundThrowsShortyException(){
        //Arrange
        var accountId = "Karlo";
        var exceptionMessage = "User not found";

        when(userDAO.findById(accountId)).thenThrow(new NoSuchElementException(exceptionMessage));

        //Act & Assert
        Assertions.assertThrows(ShortyException.class, () -> {
            userService.checkIsCorrectCredentials(accountId, "SomeFakePassword");
        });
    }

    @Test
    public void checkIsCorrectCredentialsWhenCorrectPasswordAndCorrectAccountIdIsEntered(){

        //Arrange
        var accountId = "Karlo";
        var password = "ojgisfsd";

        var user = new UserModel();
        user.setAccountId(accountId);
        user.setPassword(userService.encryptPassword(password));

        when(userDAO.findById(accountId)).thenReturn(user);

        //Act & Assert
        Assertions.assertDoesNotThrow(() -> {
            userService.checkIsCorrectCredentials(accountId, password);
        });
    }

    @Test
    public void checkIsCorrectCredentialsWhenIncorrectPasswordIsEnteredThrowsShortyException(){

        //Arrange
        var accountId = "Karlo";
        var password = "ojgisfsd";

        var user = new UserModel();
        user.setAccountId(accountId);
        user.setPassword(userService.encryptPassword(password));

        when(userDAO.findById(accountId)).thenReturn(user);

        //Act & Assert
        Assertions.assertThrows(ShortyException.class, () -> {
            userService.checkIsCorrectCredentials(accountId, "SomeFakePassword");
        });
    }

    @Test
    public void checkIsCorrectCredentialsWhenIncorrectAccountIdIsEnteredThrowsShortyException(){

        //Arrange
        var accountId = "Karlo";
        var password = "ojgisfsd";

        var user = new UserModel();
        user.setAccountId(accountId);
        user.setPassword(userService.encryptPassword(password));

        var user2 = new UserModel();
        user2.setAccountId("FakeId");
        user2.setPassword(userService.encryptPassword("FakePassword20390_fdso"));

        when(userDAO.findById(accountId)).thenReturn(user);
        when(userDAO.findById("FakeId")).thenReturn(user2);

        //Act & Assert
        Assertions.assertThrows(ShortyException.class, () -> {
            userService.checkIsCorrectCredentials("FakeId", password);
        });
    }

    @Test
    public void encryptPasswordEncodesCorrectly(){
        //Arrange
        var password = "ojgisfsd";

        //Act
        var result = userService.encryptPassword(password);

        //Assert
        Assertions.assertNotEquals(password, result);
    }

    @Test
    public void validateAndCreateNewUserWhenValidationFailsThrowsNewShortyException(){
        //Arrange
        var accountId = "123456789kakajdosit123456789kakajdosit123456789kakajdosit";
        var hashedPassword = "Kodjokdl989_fei";

        //Act & Assert
        Assertions.assertThrows(ShortyException.class,
                () -> userService.validateAndCreateNewUser(accountId, hashedPassword));
    }

    @Test
    public void validateAndCreateNewUserWhenValidationIsCorrectSavesNewUser(){
        //Arrange
        var accountId = "12345678osit";
        var hashedPassword = "Kodjokdl989_fei";

        //Act
        try {
            userService.validateAndCreateNewUser(accountId, hashedPassword);
        } catch (ShortyException e) {
            throw new RuntimeException(e);
        }

        //Act & Assert
        Mockito.verify(userDAO, times(1)).save(any(UserModel.class));
    }

    @Test
    public void generateRandomPasswordGeneratesPasswordOfLength15(){

        //Arrange
        var accountId = "Karlo";
        var password = "ojgisfsd";

        var user = new UserModel();
        user.setAccountId(accountId);
        user.setPassword("Random");

        when(userDAO.existsByPassword(any())).thenReturn(false);

        //Act
        String result = userService.generateRandomPassword();

        //Assert
        Assertions.assertEquals(result.length(), 15);
    }
}
