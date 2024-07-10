package asee.shortyapplication.adapters;

import asee.shortyapplication.ShortyApplication;
import asee.shortyapplication.shorty.dao.IUserShortyDAO;
import asee.shortycore.models.authentication.UserModel;
import asee.shortycore.models.shorty.ShortyModel;
import asee.shortycore.models.shorty.UserShortyModel;
import asee.shortydb.postgres.entities.Shorty;
import asee.shortydb.postgres.entities.UserEntity;
import asee.shortydb.postgres.entities.UserShorty;
import asee.shortydb.postgres.repositories.IUserShortyRepository;
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

@SpringBootTest(classes = ShortyApplication.class)
public class UserShortyDAOAdapterTests {
    @Autowired
    private IUserShortyDAO userShortyDAO;

    @MockBean
    private IUserShortyRepository userShortyRepository;

    @Test
    public void existsByUserEntityAccountIdAndShortyOriginalUrlFindsUserShorty(){
        //Arrange
        var accountId = "Karlo";
        var url = "https://www.google.com";

        when(userShortyRepository.existsByUserEntityAccountIdAndShortyOriginalUrl(accountId, url)).thenReturn(true);

        //Act
        var response = userShortyDAO.existsByUserEntityAccountIdAndShortyOriginalUrl(accountId, url);

        //Assert
        Assertions.assertTrue(response);
    }

    @Test
    public void existsByUserEntityAccountIdAndShortyOriginalUrlNoUserShortyFound(){
        //Arrange
        var accountId = "Karlo";
        var url = "https://www.google.com";

        when(userShortyRepository.existsByUserEntityAccountIdAndShortyOriginalUrl(accountId, url)).thenReturn(true);

        //Act
        var response = userShortyDAO.existsByUserEntityAccountIdAndShortyOriginalUrl("RandomId", url);

        //Assert
        Assertions.assertFalse(response);
    }

    @Test
    public void saveUserShortySavesCorrectly(){
        //Arrange
        var user = new UserModel();
        user.setAccountId("Karlo");

        var shorty = new ShortyModel();
        shorty.setId(1);
        shorty.setHashedUrl("Hashed1");
        shorty.setOriginalUrl("https://www.google.com");
        shorty.setRedirectionType(301);

        var userShorty = new UserShortyModel();
        userShorty.setShorty(shorty);
        userShorty.setUser(user);
        userShorty.setCounter(9);

        //Act
        userShortyDAO.save(userShorty);

        //Assert
        Mockito.verify(userShortyRepository, times(1)).save(any(UserShorty.class));
    }

    @Test
    public void findByUserShortyIdThrowsNoSuchElementException(){
        //Arrange
        when(userShortyRepository.findById(any())).thenThrow(new NoSuchElementException());

        //Act & Assert
        Assertions.assertThrows(NoSuchElementException.class, () ->
            userShortyDAO.findByUserShortyId("Karlo", 1));
    }

    @Test
    public void findByUserShortyIdReturnsUserShorty(){
        //Arrange
        var user = new UserEntity();
        user.setAccountId("Karlo");

        var shorty = new Shorty();
        shorty.setId(1);
        shorty.setHashedUrl("Hashed1");
        shorty.setOriginalUrl("https://www.google.com");
        shorty.setRedirectionType(301);

        var userShorty = new UserShorty();
        userShorty.setShorty(shorty);
        userShorty.setUserEntity(user);
        userShorty.setCounter(9);

        when(userShortyRepository.findById(any())).thenReturn(Optional.of(userShorty));

        //Act
        var response = userShortyDAO.findByUserShortyId(user.getAccountId(), shorty.getId());

        //Assert
        Assertions.assertEquals(user.getAccountId(), response.getUser().getAccountId());
        Assertions.assertEquals(shorty.getId(), response.getShorty().getId());
    }

    @Test
    public void findAllByUserEntityAccountIdWithShortyReturnsEmptyList(){
        //Arrange
        var accountId = "Karlo";

        when(userShortyRepository.findAllByUserEntityAccountIdWithShorty(any())).thenReturn(Collections.emptyList());

        //Act
        var response = userShortyDAO.findAllByUserEntityAccountIdWithShorty(accountId);

        //Assert
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    public void findAllByUserEntityAccountIdWithShortyReturnsPopulatedList(){
        //Arrange
        var user = new UserEntity();
        user.setAccountId("Karlo");

        var shorty = new Shorty();
        shorty.setId(1);
        shorty.setHashedUrl("Hashed1");
        shorty.setOriginalUrl("https://www.google.com");
        shorty.setRedirectionType(301);

        var shorty2 = new Shorty();
        shorty2.setId(2);
        shorty2.setHashedUrl("Hashed2");
        shorty2.setOriginalUrl("https://www.google.com2");
        shorty2.setRedirectionType(302);

        var userShorty = new UserShorty();
        userShorty.setShorty(shorty);
        userShorty.setUserEntity(user);
        userShorty.setCounter(9);

        var userShorty2 = new UserShorty();
        userShorty2.setShorty(shorty2);
        userShorty2.setUserEntity(user);
        userShorty2.setCounter(91);

        List<UserShorty> userShorties = new ArrayList<>();
        userShorties.add(userShorty);
        userShorties.add(userShorty2);

        when(userShortyRepository.findAllByUserEntityAccountIdWithShorty(any())).thenReturn(userShorties);

        //Act
        var response = userShortyDAO.findAllByUserEntityAccountIdWithShorty(user.getAccountId());

        //Assert
        Assertions.assertEquals(2, response.size());
        Assertions.assertTrue(response.stream().anyMatch(x -> x.getCounter() == 9));
        Assertions.assertTrue(response.stream().anyMatch(x -> x.getCounter() == 91));
    }
}
