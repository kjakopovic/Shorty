package asee.asee.unit;

import asee.asee.administration.models.Shorty;
import asee.asee.administration.models.UserEntity;
import asee.asee.administration.models.UserShorty;
import asee.asee.administration.models.UserShortyId;
import asee.asee.administration.repositories.IShortyRepository;
import asee.asee.administration.repositories.IUserRepository;
import asee.asee.administration.repositories.IUserShortyRepository;
import asee.asee.administration.responseDtos.ResolvedHashResponse;
import asee.asee.administration.services.ShortyService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShortyServiceTests {

    @Autowired
    private ShortyService shortyService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private IShortyRepository shortyRepository;

    @MockBean
    private IUserRepository userRepository;

    @MockBean
    private IUserShortyRepository userShortyRepository;

    @Test
    public void shortenTheUrlWhenUserIsNotFoundThrowsException(){
        //Arrange
        String accountId = "Karlo";
        String exceptionMessage = "User not found";

        when(userRepository.findById(accountId)).thenThrow(new NoSuchElementException(exceptionMessage));

        //Act & Assert
        Assertions.assertThrows(Exception.class,
                () -> shortyService.shortenTheUrl("url", 301, accountId),
                exceptionMessage);
    }

    @Test
    public void shortenTheUrlWhenShortyConnectionAlreadyFoundAndSentDifferentRedirectionTypeThrowsException(){
        //Arrange
        UserEntity user = new UserEntity();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        Shorty shorty = new Shorty();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(301);
        shorty.setId(1);

        Shorty shorty2 = new Shorty();
        shorty2.setHashedUrl("HashedUrl");
        shorty2.setOriginalUrl("Url");
        shorty2.setRedirectionType(302);
        shorty2.setId(2);

        List<Shorty> shorties = new ArrayList<>();
        shorties.add(shorty);
        shorties.add(shorty2);

        when(userRepository.findById(user.getAccountId())).thenReturn(Optional.of(user));
        when(shortyRepository.findShortiesByOriginalUrl(shorty.getOriginalUrl()))
                .thenReturn(shorties);
        when(userShortyRepository
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
        UserEntity user = new UserEntity();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        Shorty shorty = new Shorty();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(301);
        shorty.setId(1);

        List<Shorty> shorties = new ArrayList<>();
        shorties.add(shorty);

        when(userRepository.findById(user.getAccountId())).thenReturn(Optional.of(user));
        when(shortyRepository.findShortiesByOriginalUrl(shorty.getOriginalUrl()))
                .thenReturn(shorties);
        when(userShortyRepository
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
        UserEntity user = new UserEntity();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        Shorty shorty = new Shorty();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(301);
        shorty.setId(1);

        List<Shorty> shorties = new ArrayList<>();
        shorties.add(shorty);

        when(userRepository.findById(user.getAccountId())).thenReturn(Optional.of(user));
        when(shortyRepository.findShortiesByOriginalUrl(shorty.getOriginalUrl()))
                .thenReturn(shorties);
        when(userShortyRepository
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
        UserEntity user = new UserEntity();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        Shorty shorty = new Shorty();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(302);
        shorty.setId(1);

        List<Shorty> shorties = new ArrayList<>();
        shorties.add(shorty);

        when(userRepository.findById(user.getAccountId())).thenReturn(Optional.of(user));
        when(shortyRepository.findShortiesByOriginalUrl(shorty.getOriginalUrl()))
                .thenReturn(shorties);
        when(userShortyRepository
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

        UserEntity user = new UserEntity();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        Shorty shorty = new Shorty();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(302);
        shorty.setId(1);

        when(shortyRepository.findByHashedUrl(shorty.getHashedUrl()))
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

        UserEntity user = new UserEntity();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        Shorty shorty = new Shorty();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(302);
        shorty.setId(1);

        UserShortyId userShortyId = new UserShortyId(user.getAccountId(), shorty.getId());

        when(shortyRepository.findByHashedUrl(shorty.getHashedUrl()))
                .thenReturn(Optional.of(shorty));

        when(userShortyRepository.findById(userShortyId))
                .thenThrow(new NoSuchElementException(errorMessage));

        //Act & Assert
        Assertions.assertThrows(Exception.class,
                () -> shortyService.resolveTheHashedUrl("HashedUrl", user.getAccountId()),
                errorMessage);
    }

    @Test
    public void resolveTheHashedUrlCorrectResponse() {
        //Arrange
        UserEntity user = new UserEntity();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        Shorty shorty = new Shorty();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(302);
        shorty.setId(1);

        UserShorty userShorty = new UserShorty();
        userShorty.setId(new UserShortyId(user.getAccountId(), shorty.getId()));
        userShorty.setShorty(shorty);
        userShorty.setUserEntity(user);
        userShorty.setCounter(0);

        ResolvedHashResponse response = new ResolvedHashResponse(shorty.getOriginalUrl(), shorty.getRedirectionType());

        when(shortyRepository.findByHashedUrl(shorty.getHashedUrl()))
                .thenReturn(Optional.of(shorty));

        when(userShortyRepository.findById(any()))
                .thenReturn(Optional.of(userShorty));

        //Act
        ResolvedHashResponse result = shortyService.resolveTheHashedUrl("HashedUrl", user.getAccountId());

        //Assert
        Assertions.assertEquals(result, response);
    }

    @Test
    public void getUsersShortyStatisticsEmptyListResponse() {
        //Arrange
        Map<String, Integer> response = new HashMap<>();

        when(userShortyRepository.findAllByUserEntityAccountIdWithShorty(any()))
                .thenReturn(Collections.emptyList());

        //Act
        Map<String, Integer> result = shortyService.getUsersShortyStatistics("RandomUser");

        //Assert
        Assertions.assertEquals(result, response);
    }

    @Test
    public void getUsersShortyStatisticsReturnsPopulatedListResponse() {
        //Arrange
        UserEntity user = new UserEntity();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        Shorty shorty1 = new Shorty();
        shorty1.setHashedUrl("HashedUrl");
        shorty1.setOriginalUrl("Url");
        shorty1.setRedirectionType(302);
        shorty1.setId(1);

        Shorty shorty2 = new Shorty();
        shorty2.setHashedUrl("HashedUrl2");
        shorty2.setOriginalUrl("Url2");
        shorty2.setRedirectionType(301);
        shorty2.setId(2);

        Shorty shorty3 = new Shorty();
        shorty3.setHashedUrl("HashedUrl3");
        shorty3.setOriginalUrl("Url3");
        shorty3.setRedirectionType(302);
        shorty3.setId(3);

        UserShorty userShorty1 = new UserShorty();
        userShorty1.setId(new UserShortyId(user.getAccountId(), 1));
        userShorty1.setShorty(shorty1);
        userShorty1.setUserEntity(user);
        userShorty1.setCounter(0);

        UserShorty userShorty2 = new UserShorty();
        userShorty2.setId(new UserShortyId(user.getAccountId(), 2));
        userShorty2.setShorty(shorty2);
        userShorty2.setUserEntity(user);
        userShorty2.setCounter(5);

        UserShorty userShorty3 = new UserShorty();
        userShorty3.setId(new UserShortyId(user.getAccountId(), 3));
        userShorty3.setShorty(shorty3);
        userShorty3.setUserEntity(user);
        userShorty3.setCounter(100);

        List<UserShorty> usershorties = new ArrayList<>();
        usershorties.add(userShorty1);
        usershorties.add(userShorty2);
        usershorties.add(userShorty3);

        Map<String, Integer> response = new HashMap<>();

        response.put(shorty1.getOriginalUrl(), userShorty1.getCounter());
        response.put(shorty2.getOriginalUrl(), userShorty2.getCounter());
        response.put(shorty3.getOriginalUrl(), userShorty3.getCounter());

        when(userShortyRepository.findAllByUserEntityAccountIdWithShorty(any()))
                .thenReturn(usershorties);

        //Act
        Map<String, Integer> result = shortyService.getUsersShortyStatistics(user.getAccountId());

        //Assert
        Assertions.assertEquals(result, response);
    }
}
