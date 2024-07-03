package asee.shortydb.postgres.utils;

import asee.shortycore.models.authentication.UserModel;
import asee.shortycore.models.shorty.ShortyModel;
import asee.shortycore.models.shorty.UserShortyModel;
import asee.shortydb.postgres.entities.Shorty;
import asee.shortydb.postgres.entities.UserEntity;
import asee.shortydb.postgres.entities.UserShorty;
import asee.shortydb.postgres.entities.UserShortyId;
import org.springframework.stereotype.Service;

@Service
public class Converters implements IConverters {
    public UserModel convertUserEntityToUserModel(UserEntity entity){
        var userModel = new UserModel();

        userModel.setAccountId(entity.getAccountId());
        userModel.setPassword(entity.getPassword());

        return userModel;
    }

    public UserEntity convertUserModelToUserEntity(UserModel model){
        UserEntity userEntity = new UserEntity();

        userEntity.setAccountId(model.getAccountId());
        userEntity.setPassword(model.getPassword());

        return userEntity;
    }

    public ShortyModel convertShortyEntityToShortyModel(Shorty entity){
        var shortyModel = new ShortyModel();

        shortyModel.setId(entity.getId());
        shortyModel.setHashedUrl(entity.getHashedUrl());
        shortyModel.setOriginalUrl(entity.getOriginalUrl());
        shortyModel.setRedirectionType(entity.getRedirectionType());

        return shortyModel;
    }

    public Shorty convertShortyModelToShortyEntity(ShortyModel model){
        var shortyEntity = new Shorty();

        shortyEntity.setOriginalUrl(model.getOriginalUrl());
        shortyEntity.setHashedUrl(model.getHashedUrl());
        shortyEntity.setRedirectionType(model.getRedirectionType());
        shortyEntity.setId(model.getId());

        return shortyEntity;
    }

    public UserShortyModel convertUserShortyEntityToUserShortyModel(UserShorty entity){
        var userShortyModel = new UserShortyModel();

        userShortyModel.setCounter(entity.getCounter());
        userShortyModel.setUser(convertUserEntityToUserModel(entity.getUserEntity()));
        userShortyModel.setShorty(convertShortyEntityToShortyModel(entity.getShorty()));

        return userShortyModel;
    }

    public UserShorty convertUserShortyModelToUserShortyEntity(UserShortyModel model){
        var userShortyId = new UserShortyId(model.getUser().getAccountId(), model.getShorty().getId());

        var userShorty = new UserShorty();
        userShorty.setId(userShortyId);
        userShorty.setCounter(model.getCounter());
        userShorty.setUserEntity(convertUserModelToUserEntity(model.getUser()));
        userShorty.setShorty(convertShortyModelToShortyEntity(model.getShorty()));

        return userShorty;
    }
}
