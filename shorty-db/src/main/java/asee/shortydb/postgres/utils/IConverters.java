package asee.shortydb.postgres.utils;

import asee.shortycore.models.authentication.UserModel;
import asee.shortycore.models.shorty.ShortyModel;
import asee.shortycore.models.shorty.UserShortyModel;
import asee.shortydb.postgres.entities.Shorty;
import asee.shortydb.postgres.entities.UserEntity;
import asee.shortydb.postgres.entities.UserShorty;

public interface IConverters {
    UserModel convertUserEntityToUserModel(UserEntity entity);

    UserEntity convertUserModelToUserEntity(UserModel model);

    ShortyModel convertShortyEntityToShortyModel(Shorty entity);

    Shorty convertShortyModelToShortyEntity(ShortyModel model);

    UserShortyModel convertUserShortyEntityToUserShortyModel(UserShorty entity);

    UserShorty convertUserShortyModelToUserShortyEntity(UserShortyModel model);
}
