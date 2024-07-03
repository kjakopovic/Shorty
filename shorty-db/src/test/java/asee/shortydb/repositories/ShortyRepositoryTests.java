package asee.shortydb.repositories;

import asee.shortydb.postgres.ShortyDbApplication;
import asee.shortydb.postgres.entities.Shorty;
import asee.shortydb.postgres.repositories.IShortyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Optional;

@SpringBootTest(classes = ShortyDbApplication.class)
public class ShortyRepositoryTests {
    @Autowired
    private IShortyRepository shortyRepository;

    @BeforeEach
    public void setup(){
        var shortyEntity = new Shorty();
        shortyEntity.setId(1);
        shortyEntity.setRedirectionType(301);
        shortyEntity.setHashedUrl("Hash");
        shortyEntity.setOriginalUrl("Url");

        shortyRepository.save(shortyEntity);
    }

    @Test
    public void findByOriginalUrlAndRedirectionTypeWhenNotFoundReturnsEmptyOptional() {
        // Arrange

        // Act
        var response = shortyRepository.findByOriginalUrlAndRedirectionType("Url", 302);

        // Assert
        Assertions.assertEquals(Optional.empty(), response);
    }

    @Test
    public void findByOriginalUrlAndRedirectionTypeWhenFoundReturnsShortyClass() {
        // Arrange

        // Act
        var response = shortyRepository.findByOriginalUrlAndRedirectionType("Url", 301);

        // Assert
        Assertions.assertNotEquals(Optional.empty(), response);
        Assertions.assertTrue(response.isPresent());
        Assertions.assertEquals(1, (int) response.get().getId());
    }

    @Test
    public void findShortiesByOriginalUrlWhenShortiesNotFoundReturnsEmptyList() {
        // Arrange

        // Act
        var response = shortyRepository.findShortiesByOriginalUrl("Url1000");

        // Assert
        Assertions.assertEquals(Collections.emptyList(), response);
    }

    @Test
    public void findShortiesByOriginalUrlWhenShortiesFoundReturnsPopulatedList() {
        // Arrange

        // Act
        var response = shortyRepository.findShortiesByOriginalUrl("Url");

        // Assert
        Assertions.assertNotEquals(Collections.emptyList(), response);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    public void findByHashedUrlWhenShortyNotFoundReturnsEmptyOptional() {
        // Arrange

        // Act
        var response = shortyRepository.findByHashedUrl("Url123");

        // Assert
        Assertions.assertEquals(Optional.empty(), response);
    }

    @Test
    public void findByHashedUrlWhenShortyFoundReturnsShortyClass() {
        // Arrange

        // Act
        var response = shortyRepository.findByHashedUrl("Hash");

        // Assert
        Assertions.assertNotEquals(Optional.empty(), response);
        Assertions.assertTrue(response.isPresent());
        Assertions.assertEquals(1, (int) response.get().getId());
    }
}
