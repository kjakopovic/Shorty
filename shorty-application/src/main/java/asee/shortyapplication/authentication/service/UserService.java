package asee.shortyapplication.authentication.service;

import asee.shortyapplication.authentication.dao.IUserDAO;
import asee.shortyapplication.authentication.interfaces.IUserService;
import asee.shortycore.exceptions.ShortyException;
import asee.shortycore.models.authentication.UserModel;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.NoSuchElementException;


@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private static final Logger logger = LogManager.getLogger();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

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

    public String generateRandomPassword() {
        logger.info("Starting to generate random password.");
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        logger.info("Generating characters.");
        for (int i = 0; i < 15; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        if(userDao.existsByPassword(password.toString())){
            logger.info("Password already exists, start the generation again.");
            return generateRandomPassword();
        }

        logger.info("Return the generated password.");
        return password.toString();
    }

    public void validateAndCreateNewUser(String accountId, String password) throws ShortyException {
        logger.info("Creating new user.");
        var user = new UserModel();
        user.setAccountId(accountId);
        user.setPassword(password);

        logger.info("Checking validations.");
        var violations = validator.validate(user);

        if (!violations.isEmpty()) {
            logger.info("Violations are found.");
            StringBuilder errorMessages = new StringBuilder();
            for (var violation : violations) {
                errorMessages.append(violation.getMessage()).append(", ");
            }
            errorMessages.delete(errorMessages.length() - 2, errorMessages.length() - 1);

            logger.error("Validation failed: {}", errorMessages.toString());
            throw new ShortyException("User validation failed", errorMessages.toString());
        }

        logger.info("User created and validation succeeded.");
        userDao.save(user);
    }

    public String encryptPassword(String password) {
        logger.info("Encoding the password.");
        return passwordEncoder.encode(password);
    }

    public void checkIsCorrectCredentials(String accountId, String password) throws ShortyException {
        logger.info("Checking credentials.");
        UserModel user;

        try {
            logger.info("Trying to find user with account id {}.", accountId);
            user = userDao.findById(accountId);
        }catch (NoSuchElementException e){
            throw new ShortyException("User with account id {} does not exist or incorrect credentials!", accountId);
        }

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new ShortyException("User with account id {} does not exist or incorrect credentials!", accountId);
        }
    }
}
