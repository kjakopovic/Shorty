package asee.shortyapplication.shorty.dao;

import asee.shortycore.models.shorty.ShortyModel;

import java.util.List;
import java.util.NoSuchElementException;

public interface IShortyDAO {
    List<ShortyModel> findShortiesByOriginalUrl(String url);

    Integer save(ShortyModel shorty);

    ShortyModel findByHashedUrl(String hashedUrl) throws NoSuchElementException;
}
