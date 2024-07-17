package asee.shortyapplication.adapters;

import asee.shortyapplication.shorty.dao.IUserDAO;
import asee.shortycore.models.authentication.UserModel;
import asee.shortydb.postgres.entities.UserEntity;
import asee.shortydb.postgres.repositories.IUserRepository;
import asee.shortydb.postgres.utils.IConverters;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class UserDAOAdapter implements IUserDAO {
    private final IUserRepository userRepository;
    private final IConverters converters;

    @Override
    public boolean existsById(String accountId) {
        return userRepository.existsById(accountId);
    }

    @Override
    public String save(UserModel user) {
        var userEntity = converters.convertUserModelToUserEntity(user);

        if(user.getAccountId() == null || !userRepository.existsById(user.getAccountId())){
            userRepository.save(userEntity);
        }

        return userEntity.getAccountId();
    }

    @Override
    public UserModel findById(String accountId) throws NoSuchElementException {
        UserEntity userEntity = userRepository.findById(accountId).orElseThrow();

        return converters.convertUserEntityToUserModel(userEntity);
    }
}
