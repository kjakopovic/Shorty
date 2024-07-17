package asee.shortyapplication.shorty.interfaces;

import asee.shortycore.exceptions.ShortyException;

public interface IUserService {
    void throwIfUserExists(String accountId) throws ShortyException;

    String encryptPassword(String password);

    String getLoggedInUsersAccountId();
}
