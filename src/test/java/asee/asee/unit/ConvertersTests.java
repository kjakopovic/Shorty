package asee.asee.unit;

import asee.asee.adapters.out.postgres.entities.Shorty;
import asee.asee.adapters.out.postgres.entities.UserEntity;
import asee.asee.adapters.out.postgres.entities.UserShorty;
import asee.asee.adapters.out.postgres.entities.UserShortyId;
import asee.asee.adapters.out.postgres.utils.Converters;
import asee.asee.application.authentification.model.UserModel;
import asee.asee.application.shorty.model.ShortyModel;
import asee.asee.application.shorty.model.UserShortyModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConvertersTests {
    private UserEntity userEntity;
    private UserShorty userShortyEntity;
    private Shorty shortyEntity;
    private UserModel userModel;
    private ShortyModel shortyModel;
    private UserShortyModel userShortyModel;

    @BeforeEach
    public void setup(){
        userEntity = new UserEntity();
        userEntity.setAccountId("testAccountId");
        userEntity.setPassword("testPassword");

        userModel = new UserModel();
        userModel.setAccountId("testAccountId");
        userModel.setPassword("testPassword");

        shortyEntity = new Shorty();
        shortyEntity.setId(1);
        shortyEntity.setRedirectionType(301);
        shortyEntity.setHashedUrl("Hashed");
        shortyEntity.setOriginalUrl("Url");

        shortyModel = new ShortyModel();
        shortyModel.setId(1);
        shortyModel.setRedirectionType(301);
        shortyModel.setHashedUrl("Hashed");
        shortyModel.setOriginalUrl("Url");

        userShortyEntity = new UserShorty();
        userShortyEntity.setUserEntity(userEntity);
        userShortyEntity.setShorty(shortyEntity);
        userShortyEntity.setCounter(99);
        userShortyEntity.setId(new UserShortyId(userEntity.getAccountId(), shortyEntity.getId()));

        userShortyModel = new UserShortyModel();
        userShortyModel.setUser(userModel);
        userShortyModel.setShorty(shortyModel);
        userShortyModel.setCounter(99);
    }

    @Test
    public void convertUserEntityToUserModel() {
        // Arrange

        // Act
        var actualModel = Converters.convertUserEntityToUserModel(userEntity);

        // Assert
        Assertions.assertEquals(userModel.getAccountId(), actualModel.getAccountId());
        Assertions.assertEquals(userModel.getPassword(), actualModel.getPassword());
    }

    @Test
    public void convertUserModelToUserEntity() {
        // Arrange

        // Act
        var actualEntity = Converters.convertUserModelToUserEntity(userModel);

        // Assert
        Assertions.assertEquals(userEntity.getAccountId(), actualEntity.getAccountId());
        Assertions.assertEquals(userEntity.getPassword(), actualEntity.getPassword());
    }

    @Test
    public void convertShortyEntityToShortyModel() {
        // Arrange

        // Act
        var actualModel = Converters.convertShortyEntityToShortyModel(shortyEntity);

        // Assert
        Assertions.assertEquals(shortyModel.getId(), actualModel.getId());
        Assertions.assertEquals(shortyModel.getRedirectionType(), actualModel.getRedirectionType());
        Assertions.assertEquals(shortyModel.getHashedUrl(), actualModel.getHashedUrl());
        Assertions.assertEquals(shortyModel.getOriginalUrl(), actualModel.getOriginalUrl());
    }

    @Test
    public void convertShortyModelToShortyEntity() {
        // Arrange

        // Act
        var actualEntity = Converters.convertShortyModelToShortyEntity(shortyModel);

        // Assert
        Assertions.assertEquals(shortyEntity.getId(), actualEntity.getId());
        Assertions.assertEquals(shortyEntity.getRedirectionType(), actualEntity.getRedirectionType());
        Assertions.assertEquals(shortyEntity.getHashedUrl(), actualEntity.getHashedUrl());
        Assertions.assertEquals(shortyEntity.getOriginalUrl(), actualEntity.getOriginalUrl());
    }

    @Test
    public void convertUserShortyEntityToUserShortyModel() {
        // Arrange

        // Act
        var actualModel = Converters.convertUserShortyEntityToUserShortyModel(userShortyEntity);

        // Assert
        Assertions.assertEquals(userShortyModel.getCounter(), actualModel.getCounter());
        Assertions.assertEquals(userShortyModel.getUser().getAccountId(), actualModel.getUser().getAccountId());
        Assertions.assertEquals(userShortyModel.getShorty().getId(), actualModel.getShorty().getId());
    }

    @Test
    public void convertUserShortyModelToUserShortyEntity() {
        // Arrange

        // Act
        var actualEntity = Converters.convertUserShortyModelToUserShortyEntity(userShortyModel);

        // Assert
        Assertions.assertEquals(userShortyEntity.getCounter(), actualEntity.getCounter());
        Assertions.assertEquals(userShortyEntity.getId().getUserEntityId(), actualEntity.getId().getUserEntityId());
        Assertions.assertEquals(userShortyEntity.getId().getShortyId(), actualEntity.getId().getShortyId());
    }
}
