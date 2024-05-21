package asee.asee.administration.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @Length(max = 50)
    private String accountId;

    private String password;
}
