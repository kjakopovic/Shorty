package asee.asee.administration.services;

import asee.asee.administration.models.UserEntity;
import asee.asee.administration.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class UserService {

    private PasswordEncoder passwordEncoder;

    private IUserRepository userRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

    @Autowired
    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean CheckIfUserExists(String accountId){
        return userRepository.existsById(accountId);
    }

    public String generateRandomPassword() {
        Random random = new Random();
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

    public void addNewUser(UserEntity user) {
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
