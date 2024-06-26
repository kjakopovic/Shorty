package asee.asee.application.shorty.dao;

import asee.asee.application.shorty.model.UserShortyModel;

import java.util.List;
import java.util.NoSuchElementException;

public interface IUserShortyDAO {
    boolean existsByUserEntityAccountIdAndShortyOriginalUrl(String accountId, String url);

    void save(UserShortyModel userShorty);

    UserShortyModel findByUserShortyId(String accountId, Integer shortyId) throws NoSuchElementException;

    List<UserShortyModel> findAllByUserEntityAccountIdWithShorty(String accountId);
}
