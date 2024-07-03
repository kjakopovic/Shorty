package asee.shortydb.repositories;

import asee.shortydb.postgres.ShortyDbApplication;
import asee.shortydb.postgres.entities.UserEntity;
import asee.shortydb.postgres.repositories.IUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ShortyDbApplication.class)
public class UserRepositoryTests {
    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    public void setup(){
        var userEntity = new UserEntity();
        userEntity.setAccountId("Karlo");
        userEntity.setPassword("OPASKFOPDSKJwjeeaifjdos");

        userRepository.save(userEntity);
    }

    @Test
    public void existsByPasswordReturnsFalseWhenThereIsNoUserWithThatPassword() {
        // Arrange

        // Act
        var response = userRepository.existsByPassword("test");

        // Assert
        Assertions.assertFalse(response);
    }

    @Test
    public void existsByPasswordReturnsTrueWhenThereIsUserWithThatPassword() {
        // Arrange

        // Act
        var response = userRepository.existsByPassword("OPASKFOPDSKJwjeeaifjdos");

        // Assert
        Assertions.assertTrue(response);
    }
}
