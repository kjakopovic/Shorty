package asee.shortydb.postgres.repositories;

import asee.shortydb.postgres.entities.Shorty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IShortyRepository extends JpaRepository<Shorty, Integer> {
    Optional<Shorty> findByOriginalUrlAndRedirectionType(String url, Integer redirectionType);
    @Query("SELECT s FROM Shorty s WHERE s.originalUrl = :url")
    List<Shorty> findShortiesByOriginalUrl(@Param("url") String url);
    Optional<Shorty> findByHashedUrl(String hashedUrl);
}