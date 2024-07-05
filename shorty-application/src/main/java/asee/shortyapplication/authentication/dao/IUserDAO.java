package asee.shortyapplication.authentication.dao;

import asee.shortycore.models.authentication.UserModel;

import java.util.NoSuchElementException;

public interface IUserDAO {
    boolean existsByPassword(String password);

    boolean existsById(String accountId);

    String save(UserModel user);

    UserModel findById(String accountId) throws NoSuchElementException;
}