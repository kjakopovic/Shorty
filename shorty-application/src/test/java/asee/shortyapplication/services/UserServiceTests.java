package asee.shortyapplication.services;

import asee.shortyapplication.ShortyApplication;
import asee.shortyapplication.shorty.dao.IUserDAO;
import asee.shortyapplication.shorty.interfaces.IUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest(classes = ShortyApplication.class)
public class UserServiceTests {
    @Autowired
    private IUserService userService;

    @MockBean
    private IUserDAO userDAO;

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
    @WithMockUser(username = "RandomUser")
    public void getLoggedInUsersAccountIdReturnsCorrectAccountId(){
        //Arrange

        //Act
        var resultAccountId = userService.getLoggedInUsersAccountId();

        //Assert
        Assertions.assertEquals("RandomUser", resultAccountId);
    }
}
