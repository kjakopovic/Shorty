package asee.shortyapplication.shorty.service;

import asee.shortyapplication.shorty.dao.IUserDAO;
import asee.shortyapplication.shorty.interfaces.IUserService;
import asee.shortycore.exceptions.ShortyException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private static final Logger logger = LogManager.getLogger();

    private final IUserDAO userDao;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public void throwIfUserExists(String accountId) throws ShortyException {
        logger.info("Checking if user with account id {} exists", accountId);

        if (userDao.existsById(accountId)){
            logger.error("User with account id {} already exists!", accountId);
            throw new ShortyException("User with account id " + accountId + " already exists!");
        }
    }

    public String encryptPassword(String password) {
        logger.info("Encoding the password.");
        return passwordEncoder.encode(password);
    }

    public String getLoggedInUsersAccountId(){
        logger.info("Fetching logged in user account id");
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
