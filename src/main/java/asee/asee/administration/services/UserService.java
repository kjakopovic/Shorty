package asee.asee.administration.services;

import asee.asee.PraksaAseeApplication;
import asee.asee.exceptions.ShortyException;
import asee.asee.administration.models.UserEntity;
import asee.asee.administration.repositories.IUserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.Set;


@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(PraksaAseeApplication.class);
    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final Validator validator;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

    @Autowired
    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder, Validator validator){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    public boolean checkIfUserExists(String accountId){
        logger.info("Checking if user with account id {} exists", accountId);
        return userRepository.existsById(accountId);
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

        if(userRepository.existsByPassword(password.toString())){
            logger.info("Password already exists, start the generation again.");
            return generateRandomPassword();
        }

        logger.info("Return the generated password.");
        return password.toString();
    }

    public void validateAndCreateNewUser(String accountId, String password) throws ShortyException {
        logger.info("Creating new user.");
        UserEntity user = new UserEntity();
        user.setAccountId(accountId);
        user.setPassword(password);

        logger.info("Checking validations.");
        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        if (!violations.isEmpty()) {
            logger.info("Violations are found.");
            StringBuilder errorMessages = new StringBuilder();
            for (ConstraintViolation<UserEntity> violation : violations) {
                errorMessages.append(violation.getMessage()).append(", ");
            }
            errorMessages.delete(errorMessages.length() - 2, errorMessages.length() - 1);

            logger.error("Validation failed: {}", errorMessages.toString());
            throw new ShortyException("User validation failed", errorMessages.toString());
        }

        logger.info("User created and validation succeeded.");
        userRepository.save(user);
    }

    public String encryptPassword(String password) {
        logger.info("Encoding the password.");
        return passwordEncoder.encode(password);
    }

    public boolean isCorrectCredentials(String accountId, String password) {
        logger.info("Checking credentials.");
        UserEntity user;

        try {
            logger.info("Trying to find user with account id {}.", accountId);
            user = userRepository.findById(accountId).orElseThrow();
        }catch (NoSuchElementException e){
            logger.info("User was not found. {}", e.getMessage());
            return false;
        }

        return passwordEncoder.matches(password, user.getPassword());
    }
}
