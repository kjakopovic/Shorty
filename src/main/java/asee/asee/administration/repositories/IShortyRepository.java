package asee.asee.administration.repositories;

import asee.asee.administration.models.Shorty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IShortyRepository extends JpaRepository<Shorty, Integer> {
    Optional<Shorty> findByOriginalUrlAndRedirectionType(String url, Integer redirectionType);
    List<Shorty> findByOriginalUrl(String url);
    Optional<Shorty> findByHashedUrl(String hashedUrl);
}
