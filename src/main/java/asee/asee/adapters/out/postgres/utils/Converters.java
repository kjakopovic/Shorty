package asee.asee.adapters.out.postgres.utils;

import asee.asee.adapters.out.postgres.entities.Shorty;
import asee.asee.adapters.out.postgres.entities.UserEntity;
import asee.asee.adapters.out.postgres.entities.UserShorty;
import asee.asee.adapters.out.postgres.entities.UserShortyId;
import asee.asee.application.authentification.model.UserModel;
import asee.asee.application.shorty.model.ShortyModel;
import asee.asee.application.shorty.model.UserShortyModel;

public class Converters {
    public static UserModel ConvertUserEntityToUserModel(UserEntity entity){
        var userModel = new UserModel();

        userModel.setAccountId(entity.getAccountId());
        userModel.setPassword(entity.getPassword());

        return userModel;
    }

    public static UserEntity ConvertUserModelToUserEntity(UserModel model){
        UserEntity userEntity = new UserEntity();

        userEntity.setAccountId(model.getAccountId());
        userEntity.setPassword(model.getPassword());

        return userEntity;
    }

    public static ShortyModel ConvertShortyEntityToShortyModel(Shorty entity){
        var shortyModel = new ShortyModel();

        shortyModel.setId(entity.getId());
        shortyModel.setHashedUrl(entity.getHashedUrl());
        shortyModel.setOriginalUrl(entity.getOriginalUrl());
        shortyModel.setRedirectionType(entity.getRedirectionType());

        return shortyModel;
    }

    public static Shorty ConvertShortyModelToShortyEntity(ShortyModel model){
        var shortyEntity = new Shorty();

        shortyEntity.setOriginalUrl(model.getOriginalUrl());
        shortyEntity.setHashedUrl(model.getHashedUrl());
        shortyEntity.setRedirectionType(model.getRedirectionType());
        shortyEntity.setId(model.getId());

        return shortyEntity;
    }

    public static UserShortyModel ConvertUserShortyEntityToUserShortyModel(UserShorty entity){
        var userShortyModel = new UserShortyModel();

        userShortyModel.setCounter(entity.getCounter());
        userShortyModel.setUser(ConvertUserEntityToUserModel(entity.getUserEntity()));
        userShortyModel.setShorty(ConvertShortyEntityToShortyModel(entity.getShorty()));

        return userShortyModel;
    }

    public static UserShorty ConvertUserShortyModelToUserShortyEntity(UserShortyModel model){
        var userShortyId = new UserShortyId(model.getUser().getAccountId(), model.getShorty().getId());

        var userShorty = new UserShorty();
        userShorty.setId(userShortyId);
        userShorty.setCounter(model.getCounter());
        userShorty.setUserEntity(ConvertUserModelToUserEntity(model.getUser()));
        userShorty.setShorty(ConvertShortyModelToShortyEntity(model.getShorty()));

        return userShorty;
    }
}
