package asee.shortyapplication.authentication.interfaces;

import asee.shortycore.exceptions.ShortyException;

public interface IUserService {
    void throwIfUserExists(String accountId) throws ShortyException;

    String generateRandomPassword();

    void validateAndCreateNewUser(String accountId, String password) throws ShortyException;

    String encryptPassword(String password);

    void checkIsCorrectCredentials(String accountId, String password) throws ShortyException;
}
