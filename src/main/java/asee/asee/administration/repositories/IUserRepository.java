package asee.asee.administration.repositories;

import asee.asee.administration.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByPassword(String password);
}
