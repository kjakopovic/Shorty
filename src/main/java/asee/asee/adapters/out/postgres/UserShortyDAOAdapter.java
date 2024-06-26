package asee.asee.adapters.out.postgres;

import asee.asee.adapters.out.postgres.entities.UserShortyId;
import asee.asee.adapters.out.postgres.repositories.IUserShortyRepository;
import asee.asee.adapters.out.postgres.utils.Converters;
import asee.asee.application.shorty.dao.IUserShortyDAO;
import asee.asee.application.shorty.model.UserShortyModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class UserShortyDAOAdapter implements IUserShortyDAO {
    private final IUserShortyRepository userShortyRepository;

    @Override
    public boolean existsByUserEntityAccountIdAndShortyOriginalUrl(String accountId, String url) {
        return userShortyRepository.existsByUserEntityAccountIdAndShortyOriginalUrl(accountId, url);
    }

    @Override
    public void save(UserShortyModel userShortyModel) {
        userShortyRepository.save(Converters.ConvertUserShortyModelToUserShortyEntity(userShortyModel));
    }

    @Override
    public UserShortyModel findByUserShortyId(String accountId, Integer shortyId) throws NoSuchElementException {
        var userShortyId = new UserShortyId(accountId, shortyId);

        var userShorty = userShortyRepository.findById(userShortyId).orElseThrow();

        return Converters.ConvertUserShortyEntityToUserShortyModel(userShorty);
    }

    @Override
    public List<UserShortyModel> findAllByUserEntityAccountIdWithShorty(String accountId) {
        var userShorties = userShortyRepository.findAllByUserEntityAccountIdWithShorty(accountId);

        return userShorties
                .stream()
                .map(Converters::ConvertUserShortyEntityToUserShortyModel)
                .toList();
    }
}
