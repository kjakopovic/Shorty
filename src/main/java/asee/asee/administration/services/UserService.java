package asee.asee.administration.services;

import asee.asee.administration.models.UserEntity;
import asee.asee.administration.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {

    private IUserRepository userRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

    @Autowired
    public UserService(IUserRepository userRepository){
        this.userRepository = userRepository;
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

    public void addNewUser(String accountId, String password) throws RuntimeException {
        try {
            UserEntity user = new UserEntity();
            user.setAccountId(accountId);
            user.setPassword(password);

            userRepository.save(user);
        }catch (Exception e) {
            throw new RuntimeException("Failed to add new user: " + e.getMessage(), e);
        }
    }
}
