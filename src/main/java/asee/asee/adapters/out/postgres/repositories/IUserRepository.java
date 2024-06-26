package asee.asee.adapters.out.postgres.repositories;

import asee.asee.adapters.out.postgres.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByPassword(String password);
}
