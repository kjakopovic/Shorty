package asee.shortyapplication.adapters;

import asee.shortyapplication.ShortyApplication;
import asee.shortyapplication.shorty.dao.IShortyDAO;
import asee.shortycore.models.shorty.ShortyModel;
import asee.shortydb.postgres.entities.Shorty;
import asee.shortydb.postgres.repositories.IShortyRepository;
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
public class ShortyDAOAdapterTests {
    @Autowired
    private IShortyDAO shortyDAO;

    @MockBean
    private IShortyRepository shortyRepository;

    @Test
    public void findShortiesByOriginalUrlReturnsEmptyList(){
        //Arrange
        var url = "https://www.google.com";

        when(shortyRepository.findShortiesByOriginalUrl(url)).thenReturn(Collections.emptyList());

        //Act
        var response = shortyDAO.findShortiesByOriginalUrl(url);

        //Assert
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    public void findShortiesByOriginalUrlReturnsPopulatedList(){
        //Arrange
        var url = "https://www.google.com";

        var shorty1 = new Shorty();
        shorty1.setOriginalUrl(url);
        shorty1.setHashedUrl("Hashed1");
        shorty1.setId(1);
        shorty1.setRedirectionType(301);

        var shorty2 = new Shorty();
        shorty2.setOriginalUrl(url);
        shorty2.setHashedUrl("Hashed2");
        shorty2.setId(2);
        shorty2.setRedirectionType(302);

        List<Shorty> shorties = new ArrayList<>();
        shorties.add(shorty1);
        shorties.add(shorty2);

        when(shortyRepository.findShortiesByOriginalUrl(url)).thenReturn(shorties);

        //Act
        var response = shortyDAO.findShortiesByOriginalUrl(url);

        //Assert
        Assertions.assertEquals(2, response.size());
        Assertions.assertTrue(response.stream().anyMatch(x -> x.getHashedUrl().equals("Hashed1")));
        Assertions.assertTrue(response.stream().anyMatch(x -> x.getHashedUrl().equals("Hashed2")));
    }

    @Test
    public void saveShortySavesCorrectly(){
        //Arrange
        var url = "https://www.google.com";

        var shorty = new ShortyModel();
        shorty.setOriginalUrl(url);
        shorty.setHashedUrl("Hashed1");
        shorty.setId(1);
        shorty.setRedirectionType(301);

        //Act
        var response = shortyDAO.save(shorty);

        //Assert
        Assertions.assertEquals(shorty.getId(), response);
        Mockito.verify(shortyRepository, times(1)).save(any(Shorty.class));
    }

    @Test
    public void findByHashedUrlThrowsNoSuchElementException(){
        //Arrange
        var url = "https://www.google.com";

        var shortyModel = new ShortyModel();
        shortyModel.setOriginalUrl(url);
        shortyModel.setHashedUrl("Hashed1");
        shortyModel.setId(1);
        shortyModel.setRedirectionType(301);

        when(shortyRepository.findByHashedUrl("hashed")).thenThrow(new NoSuchElementException());

        //Act & Assert
        Assertions.assertThrows(NoSuchElementException.class, () -> shortyDAO.findByHashedUrl("hashed"));
    }

    @Test
    public void findByHashedUrlReturnsShorty(){
        //Arrange
        var url = "https://www.google.com";

        var shorty = new Shorty();
        shorty.setOriginalUrl(url);
        shorty.setHashedUrl("Hashed1");
        shorty.setId(1);
        shorty.setRedirectionType(301);

        when(shortyRepository.findByHashedUrl(shorty.getHashedUrl())).thenReturn(Optional.of(shorty));

        //Act
        var response = shortyDAO.findByHashedUrl(shorty.getHashedUrl());

        //Assert
        Assertions.assertEquals(shorty.getId(), response.getId());
        Assertions.assertEquals(shorty.getOriginalUrl(), response.getOriginalUrl());
        Assertions.assertEquals(shorty.getHashedUrl(), response.getHashedUrl());
        Assertions.assertEquals(shorty.getRedirectionType(), response.getRedirectionType());
    }
}
