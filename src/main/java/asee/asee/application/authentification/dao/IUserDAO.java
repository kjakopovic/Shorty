package asee.asee.application.authentification.dao;

import asee.asee.application.authentification.model.UserModel;

import java.util.NoSuchElementException;

public interface IUserDAO {
    boolean existsByPassword(String password);

    boolean existsById(String accountId);

    String save(UserModel user);

    UserModel findById(String accountId) throws NoSuchElementException;
}