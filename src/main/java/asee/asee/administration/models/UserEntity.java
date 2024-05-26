package asee.asee.administration.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @Length(max = 50)
    private String accountId;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_shorties",
            joinColumns = @JoinColumn(name = "user_account_id", referencedColumnName = "accountId"),
            inverseJoinColumns = @JoinColumn(name = "shorty_id", referencedColumnName = "id")
    )
    private List<Shorty> shortedUrls = new ArrayList<>();
}
