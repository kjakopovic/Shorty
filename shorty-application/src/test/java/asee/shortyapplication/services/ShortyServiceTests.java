package asee.shortyapplication.services;

import asee.shortyapplication.ShortyApplication;
import asee.shortyapplication.TestConfig;
import asee.shortyapplication.authentication.dao.IUserDAO;
import asee.shortyapplication.shorty.dao.IShortyDAO;
import asee.shortyapplication.shorty.dao.IUserShortyDAO;
import asee.shortyapplication.shorty.dto.ResolvedHashResponse;
import asee.shortyapplication.shorty.interfaces.IShortyService;
import asee.shortycore.exceptions.ShortyException;
import asee.shortycore.models.authentication.UserModel;
import asee.shortycore.models.shorty.ShortyModel;
import asee.shortycore.models.shorty.UserShortyModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ShortyApplication.class, TestConfig.class})
public class ShortyServiceTests {

    @Autowired
    private IShortyService shortyService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private IShortyDAO shortyDAO;

    @MockBean
    private IUserDAO userDAO;

    @MockBean
    private IUserShortyDAO userShortyDAO;

    @Test
    public void shortenTheUrlWhenUserIsNotFoundThrowsException(){
        //Arrange
        String accountId = "Karlo";
        String exceptionMessage = "User not found";

        when(userDAO.findById(accountId)).thenThrow(new NoSuchElementException(exceptionMessage));

        //Act & Assert
        Assertions.assertThrows(Exception.class,
                () -> shortyService.shortenTheUrl("url", 301, accountId),
                exceptionMessage);
    }

    @Test
    public void shortenTheUrlWhenShortyConnectionAlreadyFoundAndSentDifferentRedirectionTypeThrowsException(){
        //Arrange
        var user = new UserModel();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        var shorty = new ShortyModel();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(301);
        shorty.setId(1);

        var shorty2 = new ShortyModel();
        shorty2.setHashedUrl("HashedUrl");
        shorty2.setOriginalUrl("Url");
        shorty2.setRedirectionType(302);
        shorty2.setId(2);

        List<ShortyModel> shorties = new ArrayList<>();
        shorties.add(shorty);
        shorties.add(shorty2);

        when(userDAO.findById(user.getAccountId())).thenReturn(user);
        when(shortyDAO.findShortiesByOriginalUrl(shorty.getOriginalUrl()))
                .thenReturn(shorties);
        when(userShortyDAO
                .existsByUserEntityAccountIdAndShortyOriginalUrl(user.getAccountId(), shorty.getOriginalUrl()))
                .thenReturn(true);

        //Act & Assert
        Assertions.assertThrows(Exception.class,
                () -> shortyService.shortenTheUrl("Url", 301, user.getAccountId()),
                "Već ste napravili taj link sa drugim redirection typeom!");
    }

    @Test
    public void shortenTheUrlWhenShortyConnectionAlreadyFoundAndSentCorrectRedirectionTypeReturnsHashedUrl() {
        //Arrange
        var user = new UserModel();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        var shorty = new ShortyModel();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(301);
        shorty.setId(1);

        List<ShortyModel> shorties = new ArrayList<>();
        shorties.add(shorty);

        when(userDAO.findById(user.getAccountId())).thenReturn(user);
        when(shortyDAO.findShortiesByOriginalUrl(shorty.getOriginalUrl()))
                .thenReturn(shorties);
        when(userShortyDAO
                .existsByUserEntityAccountIdAndShortyOriginalUrl(user.getAccountId(), shorty.getOriginalUrl()))
                .thenReturn(true);

        //Act
        String result;

        try {
            result = shortyService.shortenTheUrl("Url", 301, user.getAccountId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Assert
        Assertions.assertEquals(result, shorty.getHashedUrl());
    }

    @Test
    public void shortenTheUrlWhenShortyConnectionNotFoundAndSentCorrectRedirectionTypeReturnsHashedUrl() {
        //Arrange
        var user = new UserModel();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        var shorty = new ShortyModel();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(301);
        shorty.setId(1);

        List<ShortyModel> shorties = new ArrayList<>();
        shorties.add(shorty);

        when(userDAO.findById(user.getAccountId())).thenReturn(user);
        when(shortyDAO.findShortiesByOriginalUrl(shorty.getOriginalUrl()))
                .thenReturn(shorties);
        when(userShortyDAO
                .existsByUserEntityAccountIdAndShortyOriginalUrl(user.getAccountId(), shorty.getOriginalUrl()))
                .thenReturn(false);

        //Act
        String result;

        try {
            result = shortyService.shortenTheUrl("Url", 301, user.getAccountId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Assert
        Assertions.assertEquals(result, shorty.getHashedUrl());
    }

    @Test
    public void shortenTheUrlWhenShortyConnectionNotFoundAndSentNewRedirectionTypeReturnsNewHashedUrl() {
        //Arrange
        var user = new UserModel();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        var shorty = new ShortyModel();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(302);
        shorty.setId(1);

        List<ShortyModel> shorties = new ArrayList<>();
        shorties.add(shorty);

        when(userDAO.findById(user.getAccountId())).thenReturn(user);
        when(shortyDAO.findShortiesByOriginalUrl(shorty.getOriginalUrl()))
                .thenReturn(shorties);
        when(userShortyDAO
                .existsByUserEntityAccountIdAndShortyOriginalUrl(user.getAccountId(), shorty.getOriginalUrl()))
                .thenReturn(false);
        when(passwordEncoder.encode(shorty.getOriginalUrl()))
                .thenReturn("_3refgosgoefajveNI>cv/*-?FJIOejfijsiorjgABCDEio<srjgw9oi90'fw4jgrnogjWEIGU90irg");

        //Act
        String result;

        try {
            result = shortyService.shortenTheUrl("Url", 301, user.getAccountId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Assert
        Assertions.assertEquals(result, "ABCDE");
    }

    @Test
    public void resolveTheHashedUrlWhenShortyNotFoundThrowsException() {
        //Arrange
        String errorMessage = "Shorty not found!";

        var user = new UserModel();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        var shorty = new ShortyModel();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(302);
        shorty.setId(1);

        when(shortyDAO.findByHashedUrl(shorty.getHashedUrl()))
                .thenThrow(new NoSuchElementException(errorMessage));

        //Act & Assert
        Assertions.assertThrows(Exception.class,
                () -> shortyService.resolveTheHashedUrl("HashedUrl", user.getAccountId()),
                errorMessage);
    }

    @Test
    public void resolveTheHashedUrlWhenUserShortyNotFoundThrowsException() {
        //Arrange
        String errorMessage = "Connection not found!";

        var user = new UserModel();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        var shorty = new ShortyModel();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(302);
        shorty.setId(1);

        when(shortyDAO.findByHashedUrl(shorty.getHashedUrl()))
                .thenReturn(shorty);

        when(userShortyDAO.findByUserShortyId(user.getAccountId(), shorty.getId()))
                .thenThrow(new NoSuchElementException(errorMessage));

        //Act & Assert
        Assertions.assertThrows(Exception.class,
                () -> shortyService.resolveTheHashedUrl("HashedUrl", user.getAccountId()),
                errorMessage);
    }

    @Test
    public void resolveTheHashedUrlCorrectResponse() {
        //Arrange
        var user = new UserModel();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        var shorty = new ShortyModel();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(302);
        shorty.setId(1);

        var userShorty = new UserShortyModel();
        userShorty.setShorty(shorty);
        userShorty.setUser(user);
        userShorty.setCounter(0);

        var response = new ResolvedHashResponse(shorty.getOriginalUrl(), shorty.getRedirectionType());

        when(shortyDAO.findByHashedUrl(shorty.getHashedUrl()))
                .thenReturn(shorty);

        when(userShortyDAO.findByUserShortyId(any(), any()))
                .thenReturn(userShorty);

        //Act
        ResolvedHashResponse result;
        try {
            result = shortyService.resolveTheHashedUrl("HashedUrl", user.getAccountId());
        } catch (ShortyException e) {
            throw new RuntimeException(e);
        }

        //Assert
        Assertions.assertEquals(result.getUrl(), response.getUrl());
        Assertions.assertEquals(result.getRedirectionType(), response.getRedirectionType());
    }

    @Test
    public void getUsersShortyStatisticsEmptyListResponse() {
        //Arrange
        Map<String, Integer> response = new HashMap<>();

        when(userShortyDAO.findAllByUserEntityAccountIdWithShorty(any()))
                .thenReturn(Collections.emptyList());

        //Act
        Map<String, Integer> result = shortyService.getUsersShortyStatistics("RandomUser");

        //Assert
        Assertions.assertEquals(result, response);
    }

    @Test
    public void getUsersShortyStatisticsReturnsPopulatedListResponse() {
        //Arrange
        var user = new UserModel();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        var shorty1 = new ShortyModel();
        shorty1.setHashedUrl("HashedUrl");
        shorty1.setOriginalUrl("Url");
        shorty1.setRedirectionType(302);
        shorty1.setId(1);

        var shorty2 = new ShortyModel();
        shorty2.setHashedUrl("HashedUrl2");
        shorty2.setOriginalUrl("Url2");
        shorty2.setRedirectionType(301);
        shorty2.setId(2);

        var shorty3 = new ShortyModel();
        shorty3.setHashedUrl("HashedUrl3");
        shorty3.setOriginalUrl("Url3");
        shorty3.setRedirectionType(302);
        shorty3.setId(3);

        var userShorty1 = new UserShortyModel();
        userShorty1.setShorty(shorty1);
        userShorty1.setUser(user);
        userShorty1.setCounter(0);

        var userShorty2 = new UserShortyModel();
        userShorty2.setShorty(shorty2);
        userShorty2.setUser(user);
        userShorty2.setCounter(5);

        var userShorty3 = new UserShortyModel();
        userShorty3.setShorty(shorty3);
        userShorty3.setUser(user);
        userShorty3.setCounter(100);

        List<UserShortyModel> usershorties = new ArrayList<>();
        usershorties.add(userShorty1);
        usershorties.add(userShorty2);
        usershorties.add(userShorty3);

        Map<String, Integer> response = new HashMap<>();

        response.put(shorty1.getOriginalUrl(), userShorty1.getCounter());
        response.put(shorty2.getOriginalUrl(), userShorty2.getCounter());
        response.put(shorty3.getOriginalUrl(), userShorty3.getCounter());

        when(userShortyDAO.findAllByUserEntityAccountIdWithShorty(any()))
                .thenReturn(usershorties);

        //Act
        Map<String, Integer> result = shortyService.getUsersShortyStatistics(user.getAccountId());

        //Assert
        Assertions.assertEquals(result, response);
    }
}
