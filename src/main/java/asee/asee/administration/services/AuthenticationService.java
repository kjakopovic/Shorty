package asee.asee.administration.services;

import asee.asee.PraksaAseeApplication;
import asee.asee.exceptions.ShortyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final Logger logger = LogManager.getLogger(PraksaAseeApplication.class);
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void loginUser(String accountId, String password) throws ShortyException {
        logger.info("Starting to login the user");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(accountId, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (AuthenticationException e) {
            logger.error("Authentication exception", e);

            throw new ShortyException("Login user failed", e.getMessage());
        }

        logger.info("Authentication successful");
    }

    public String getLoggedInUsersAccountId(){
        logger.info("Fetching logged in user account id");
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
