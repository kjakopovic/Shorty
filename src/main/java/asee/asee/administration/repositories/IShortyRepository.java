package asee.asee.administration.repositories;

import asee.asee.administration.models.Shorty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IShortyRepository extends JpaRepository<Shorty, Integer> {
    boolean existsByOriginalUrl(String url);
    Optional<Shorty> findByOriginalUrlAndRedirectionType(String url, int redirectionType);
    Optional<Shorty> findByHashedUrl(String hashedUrl);
}
