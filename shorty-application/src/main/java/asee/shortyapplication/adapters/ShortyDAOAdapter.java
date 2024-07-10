package asee.shortyapplication.adapters;

import asee.shortyapplication.shorty.dao.IShortyDAO;
import asee.shortycore.models.shorty.ShortyModel;
import asee.shortydb.postgres.repositories.IShortyRepository;
import asee.shortydb.postgres.utils.Converters;
import asee.shortydb.postgres.utils.IConverters;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ShortyDAOAdapter implements IShortyDAO {
    private final IShortyRepository shortyRepository;
    private final IConverters converters;

    @Override
    public List<ShortyModel> findShortiesByOriginalUrl(String url) {
        var shorties = shortyRepository.findShortiesByOriginalUrl(url);

        return shorties
                .stream()
                .map(converters::convertShortyEntityToShortyModel)
                .toList();
    }

    @Override
    public Integer save(ShortyModel shorty) {
        var shortyEntity = converters.convertShortyModelToShortyEntity(shorty);

        if(shortyEntity.getId() == null || !shortyRepository.existsById(shortyEntity.getId())){
            shortyRepository.save(shortyEntity);
        }

        return shortyEntity.getId();
    }

    @Override
    public ShortyModel findByHashedUrl(String hashedUrl) throws NoSuchElementException {
        var shortyEntity = shortyRepository.findByHashedUrl(hashedUrl).orElseThrow();

        return converters.convertShortyEntityToShortyModel(shortyEntity);
    }
}
