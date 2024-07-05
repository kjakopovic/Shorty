package asee.shortyapplication.authentication.interfaces;

import asee.shortycore.exceptions.ShortyException;

public interface IAuthenticationService {
    void loginUser(String accountId, String password) throws ShortyException;

    String getLoggedInUsersAccountId();
}
