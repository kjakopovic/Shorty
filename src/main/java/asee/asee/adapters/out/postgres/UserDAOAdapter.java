package asee.asee.adapters.out.postgres;


import asee.asee.adapters.out.postgres.entities.UserEntity;
import asee.asee.adapters.out.postgres.repositories.IUserRepository;
import asee.asee.adapters.out.postgres.utils.Converters;
import asee.asee.application.authentification.dao.IUserDAO;
import asee.asee.application.authentification.model.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class UserDAOAdapter implements IUserDAO {
    private final IUserRepository userRepository;

    @Override
    public boolean existsByPassword(String password) {
        return userRepository.existsByPassword(password);
    }

    @Override
    public boolean existsById(String accountId) {
        return userRepository.existsById(accountId);
    }

    @Override
    public String save(UserModel user) {
        var userEntity = Converters.convertUserModelToUserEntity(user);

        userRepository.save(userEntity);

        return userEntity.getAccountId();
    }

    @Override
    public UserModel findById(String accountId) throws NoSuchElementException {
        UserEntity userEntity = userRepository.findById(accountId).orElseThrow();

        return Converters.convertUserEntityToUserModel(userEntity);
    }
}
