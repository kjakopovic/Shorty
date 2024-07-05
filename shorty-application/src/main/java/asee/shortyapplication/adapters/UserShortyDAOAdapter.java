package asee.shortyapplication.adapters;

import asee.shortyapplication.shorty.dao.IUserShortyDAO;
import asee.shortycore.models.shorty.UserShortyModel;
import asee.shortydb.postgres.entities.UserShortyId;
import asee.shortydb.postgres.repositories.IUserShortyRepository;
import asee.shortydb.postgres.utils.Converters;
import asee.shortydb.postgres.utils.IConverters;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class UserShortyDAOAdapter implements IUserShortyDAO {
    private final IUserShortyRepository userShortyRepository;
    private final IConverters converters;

    @Override
    public boolean existsByUserEntityAccountIdAndShortyOriginalUrl(String accountId, String url) {
        return userShortyRepository.existsByUserEntityAccountIdAndShortyOriginalUrl(accountId, url);
    }

    @Override
    public void save(UserShortyModel userShortyModel) {
        userShortyRepository.save(converters.convertUserShortyModelToUserShortyEntity(userShortyModel));
    }

    @Override
    public UserShortyModel findByUserShortyId(String accountId, Integer shortyId) throws NoSuchElementException {
        var userShortyId = new UserShortyId(accountId, shortyId);

        var userShorty = userShortyRepository.findById(userShortyId).orElseThrow();

        return converters.convertUserShortyEntityToUserShortyModel(userShorty);
    }

    @Override
    public List<UserShortyModel> findAllByUserEntityAccountIdWithShorty(String accountId) {
        var userShorties = userShortyRepository.findAllByUserEntityAccountIdWithShorty(accountId);

        return userShorties
                .stream()
                .map(converters::convertUserShortyEntityToUserShortyModel)
                .toList();
    }
}
