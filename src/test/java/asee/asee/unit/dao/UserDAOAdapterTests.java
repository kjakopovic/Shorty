package asee.asee.unit.dao;

import asee.asee.adapters.out.postgres.entities.UserEntity;
import asee.asee.adapters.out.postgres.repositories.IUserRepository;
import asee.asee.application.authentification.dao.IUserDAO;
import asee.asee.application.authentification.model.UserModel;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDAOAdapterTests {
    @Autowired
    private IUserDAO userDAO;

    @MockBean
    private IUserRepository userRepository;

    @Test
    public void existsByPasswordNoPasswordFound(){
        //Arrange
        var password = "Password12_fo3";

        when(userRepository.existsByPassword(password)).thenReturn(true);

        //Act
        var response = userDAO.existsByPassword("NoPassword213_");

        //Assert
        Assertions.assertFalse(response);
    }

    @Test
    public void existsByPasswordFindsPassword(){
        //Arrange
        var password = "Password12_fo3";

        when(userRepository.existsByPassword(password)).thenReturn(true);

        //Act
        var response = userDAO.existsByPassword(password);

        //Assert
        Assertions.assertTrue(response);
    }

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
        user.setPassword("Password123");
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
        user.setPassword("Password123");
        user.setAccountId("Karlo");

        when(userRepository.findById(user.getAccountId())).thenReturn(Optional.of(user));

        //Act
        var response = userDAO.findById(user.getAccountId());

        //Assert
        Assertions.assertEquals(user.getAccountId(), response.getAccountId());
        Assertions.assertEquals(user.getPassword(), response.getPassword());
    }
}
