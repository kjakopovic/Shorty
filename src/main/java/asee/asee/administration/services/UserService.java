package asee.asee.administration.services;

import asee.asee.exceptions.ShortyException;
import asee.asee.administration.models.UserEntity;
import asee.asee.administration.repositories.IUserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.Set;


@Service
public class UserService {

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
        return userRepository.existsById(accountId);
    }

    public String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 15; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        if(userRepository.existsByPassword(password.toString())){
            return generateRandomPassword();
        }

        return password.toString();
    }

    public void validateAndCreateNewUser(String accountId, String password) throws ShortyException {
        UserEntity user = new UserEntity();
        user.setAccountId(accountId);
        user.setPassword(password);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        if (!violations.isEmpty()) {
            StringBuilder errorMessages = new StringBuilder();
            for (ConstraintViolation<UserEntity> violation : violations) {
                errorMessages.append(violation.getMessage()).append(", ");
            }
            errorMessages.delete(errorMessages.length() - 2, errorMessages.length() - 1);

            throw new ShortyException("User validation failed", errorMessages.toString());
//            logger.warn("[REGISTER USER ENDPOINT] - Validation failed: " + errorMessage);
        }

        userRepository.save(user);
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean isCorrectCredentials(String accountId, String password) {
        UserEntity user;

        try {
            user = userRepository.findById(accountId).orElseThrow();
        }catch (NoSuchElementException e){
            return false;
        }

        return passwordEncoder.matches(password, user.getPassword());
    }
}
