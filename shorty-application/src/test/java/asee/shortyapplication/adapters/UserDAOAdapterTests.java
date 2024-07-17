package asee.shortyapplication.adapters;

import asee.shortyapplication.ShortyApplication;
import asee.shortyapplication.shorty.dao.IUserDAO;
import asee.shortycore.models.authentication.UserModel;
import asee.shortydb.postgres.entities.UserEntity;
import asee.shortydb.postgres.repositories.IUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShortyApplication.class)
public class UserDAOAdapterTests {
    @Autowired
    private IUserDAO userDAO;

    @MockBean
    private IUserRepository userRepository;

    @Test
    public void existsByIdFindsAccountId(){
        //Arrange
        var accountId = "Karlo";

        when(userRepository.existsById(accountId)).thenReturn(true);

        //Act
        var response = userDAO.existsById(accountId);

        //Assert
        Assertions.assertTrue(response);
    }

    @Test
    public void existsByIdNoAccountIdFound(){
        //Arrange
        var accountId = "Karlo";

        when(userRepository.existsById(accountId)).thenReturn(true);

        //Act
        var response = userDAO.existsById("Karlo2");

        //Assert
        Assertions.assertFalse(response);
    }

    @Test
    public void saveUserSavesCorrectly(){
        //Arrange
        var user = new UserModel();
        user.setAccountId("Karlo");

        //Act
        var response = userDAO.save(user);

        //Assert
        Assertions.assertEquals(user.getAccountId(), response);
        Mockito.verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void findByIdThrowsNoSuchElementException(){
        //Arrange
        when(userRepository.findById("Test")).thenThrow(new NoSuchElementException());

        //Act & Assert
        Assertions.assertThrows(NoSuchElementException.class, () -> userDAO.findById("Test"));
    }

    @Test
    public void findByIdReturnsUserEntity(){
        //Arrange
        var user = new UserEntity();
        user.setAccountId("Karlo");

        when(userRepository.findById(user.getAccountId())).thenReturn(Optional.of(user));

        //Act
        var response = userDAO.findById(user.getAccountId());

        //Assert
        Assertions.assertEquals(user.getAccountId(), response.getAccountId());
    }
}
