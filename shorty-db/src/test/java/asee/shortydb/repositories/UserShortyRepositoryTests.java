package asee.shortydb.repositories;

import asee.shortydb.postgres.ShortyDbApplication;
import asee.shortydb.postgres.entities.Shorty;
import asee.shortydb.postgres.entities.UserEntity;
import asee.shortydb.postgres.entities.UserShorty;
import asee.shortydb.postgres.entities.UserShortyId;
import asee.shortydb.postgres.repositories.IShortyRepository;
import asee.shortydb.postgres.repositories.IUserRepository;
import asee.shortydb.postgres.repositories.IUserShortyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

@SpringBootTest(classes = ShortyDbApplication.class)
public class UserShortyRepositoryTests {
    @Autowired
    private IUserShortyRepository userShortyRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IShortyRepository shortyRepository;

    @BeforeEach
    public void setup(){
        var userEntity = new UserEntity();
        userEntity.setAccountId("Karlo");

        userRepository.save(userEntity);

        var shortyEntity = new Shorty();
        shortyEntity.setId(1);
        shortyEntity.setRedirectionType(301);
        shortyEntity.setHashedUrl("Hash");
        shortyEntity.setOriginalUrl("Url");

        shortyRepository.save(shortyEntity);

        var userShorty = new UserShorty();
        userShorty.setId(new UserShortyId(userEntity.getAccountId(), shortyEntity.getId()));
        userShorty.setCounter(10);
        userShorty.setShorty(shortyEntity);
        userShorty.setUserEntity(userEntity);

        userShortyRepository.save(userShorty);
    }

    @Test
    public void existsByUserEntityAccountIdAndShortyOriginalUrlReturnsFalseWhenNoneFound() {
        // Arrange

        // Act
        var response = userShortyRepository.existsByUserEntityAccountIdAndShortyOriginalUrl("test", "Url");

        // Assert
        Assertions.assertFalse(response);
    }

    @Test
    public void existsByUserEntityAccountIdAndShortyOriginalUrlReturnsTrueWhenUserShortyFound() {
        // Arrange

        // Act
        var response = userShortyRepository.existsByUserEntityAccountIdAndShortyOriginalUrl("Karlo", "Url");

        // Assert
        Assertions.assertTrue(response);
    }

    @Test
    public void findAllByUserEntityAccountIdWithShortyReturnsEmptyListWhenNoneFound() {
        // Arrange

        // Act
        var response = userShortyRepository.findAllByUserEntityAccountIdWithShorty("test");

        // Assert
        Assertions.assertEquals(Collections.emptyList(), response);
    }

    @Test
    public void findAllByUserEntityAccountIdWithShortyReturnsPopulatedListWhenFound() {
        // Arrange

        // Act
        var response = userShortyRepository.findAllByUserEntityAccountIdWithShorty("Karlo");

        // Assert
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Url", response.get(0).getShorty().getOriginalUrl());
    }
}
