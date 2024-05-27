package asee.asee.unit;

import asee.asee.administration.models.Shorty;
import asee.asee.administration.models.UserEntity;
import asee.asee.administration.repositories.IShortyRepository;
import asee.asee.administration.repositories.IUserRepository;
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

import java.util.NoSuchElementException;
import java.util.Optional;

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

    @Test
    public void shortenTheUrl_when_userIsNotFound_throwsException(){
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
    public void shortenTheUrl_when_shortyAlreadyFound_returnsExistingShorty(){
        //Arrange
        UserEntity user = new UserEntity();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        Shorty shorty = new Shorty();
        shorty.setTimesUsed(10);
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(301);

        when(userRepository.findById(user.getAccountId())).thenReturn(Optional.of(user));
        when(shortyRepository.findByOriginalUrlAndRedirectionType(shorty.getOriginalUrl(), shorty.getRedirectionType()))
                .thenReturn(Optional.of(shorty));

        //Act
        String result = shortyService.shortenTheUrl(shorty.getOriginalUrl(), 301, user.getAccountId());

        //Assert
        Assertions.assertEquals(shorty.getHashedUrl(), result);
    }

    @Test
    public void shortenTheUrl_when_shortyNotFound_returnsNewShorty(){
        //Arrange
        UserEntity user = new UserEntity();
        user.setPassword("password1");
        user.setAccountId("Karlo");

        String url = "Url";
        int redirectionType = 301;

        when(userRepository.findById(user.getAccountId())).thenReturn(Optional.of(user));
        when(shortyRepository.findByOriginalUrlAndRedirectionType(url, redirectionType))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(url)).thenReturn("ABCDE_ekfofosg");

        //Act
        String result = shortyService.shortenTheUrl(url, redirectionType, user.getAccountId());

        //Assert
        Assertions.assertEquals("ABCDE", result);
    }

    @Test
    public void resolveTheHashedUrl_when_shortyIsNotFound_throwsException(){
        //Arrange
        String hashedUrl = "Hashe";
        String exceptionMessage = "Shorty not found";

        when(shortyRepository.findByHashedUrl(hashedUrl)).thenThrow(new NoSuchElementException(exceptionMessage));

        //Act & Assert
        Assertions.assertThrows(Exception.class,
                () -> shortyService.resolveTheHashedUrl(hashedUrl),
                exceptionMessage);
    }

    @Test
    public void resolveTheHashedUrl_when_shortyIsFound_getResolvedHashResponse(){
        //Arrange
        Shorty shorty = new Shorty();
        shorty.setHashedUrl("HashedUrl");
        shorty.setOriginalUrl("Url");
        shorty.setRedirectionType(301);
        shorty.setTimesUsed(10);

        ResolvedHashResponse response =
                new ResolvedHashResponse(shorty.getOriginalUrl(), shorty.getRedirectionType());

        when(shortyRepository.findByHashedUrl(shorty.getHashedUrl()))
                .thenReturn(Optional.of(shorty));

        //Act
        ResolvedHashResponse result = shortyService.resolveTheHashedUrl(shorty.getHashedUrl());

        //Assert
        Assertions.assertEquals(result, response);
    }
}
