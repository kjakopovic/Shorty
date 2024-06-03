package asee.asee.administration.repositories;

import asee.asee.administration.models.UserShorty;
import asee.asee.administration.models.UserShortyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IUserShortyRepository extends JpaRepository<UserShorty, UserShortyId> {
    @Query("SELECT COUNT(us) > 0 FROM UserShorty us WHERE us.userEntity.accountId = :accountId AND us.shorty.originalUrl = :url")
    boolean existsByUserEntityAccountIdAndShortyOriginalUrl(@Param("accountId") String accountId, @Param("url") String url);

    List<UserShorty> findAllByUserEntityAccountId(String accountId);

    @Query("SELECT us FROM UserShorty us JOIN FETCH us.shorty s WHERE us.userEntity.accountId = :accountId")
    List<UserShorty> findAllByUserEntityAccountIdWithShorty(@Param("accountId") String accountId);
}
