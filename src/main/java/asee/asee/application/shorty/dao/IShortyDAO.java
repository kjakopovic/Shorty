package asee.asee.application.shorty.dao;

import asee.asee.application.shorty.model.ShortyModel;

import java.util.List;
import java.util.NoSuchElementException;

public interface IShortyDAO {
    List<ShortyModel> findShortiesByOriginalUrl(String url);

    Integer save(ShortyModel shorty);

    ShortyModel findByHashedUrl(String hashedUrl) throws NoSuchElementException;
}
