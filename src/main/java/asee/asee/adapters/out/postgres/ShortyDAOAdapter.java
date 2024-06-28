package asee.asee.adapters.out.postgres;

import asee.asee.adapters.out.postgres.repositories.IShortyRepository;
import asee.asee.adapters.out.postgres.utils.Converters;
import asee.asee.application.shorty.dao.IShortyDAO;
import asee.asee.application.shorty.model.ShortyModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ShortyDAOAdapter implements IShortyDAO {
    private final IShortyRepository shortyRepository;

    @Override
    public List<ShortyModel> findShortiesByOriginalUrl(String url) {
        var shorties = shortyRepository.findShortiesByOriginalUrl(url);

        return shorties
                .stream()
                .map(Converters::convertShortyEntityToShortyModel)
                .toList();
    }

    @Override
    public Integer save(ShortyModel shorty) {
        var shortyEntity = Converters.convertShortyModelToShortyEntity(shorty);

        shortyRepository.save(shortyEntity);

        return shortyEntity.getId();
    }

    @Override
    public ShortyModel findByHashedUrl(String hashedUrl) throws NoSuchElementException {
        var shortyEntity = shortyRepository.findByHashedUrl(hashedUrl).orElseThrow();

        return Converters.convertShortyEntityToShortyModel(shortyEntity);
    }
}
