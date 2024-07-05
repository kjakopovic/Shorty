package asee.shortydb.postgres.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_shorties")
@Getter
@Setter
public class UserShorty {
    @Id
    private UserShortyId id;

    @ManyToOne
    @MapsId("userEntityId")
    @JoinColumn(name = "user_id", referencedColumnName = "accountId")
    private UserEntity userEntity;

    @ManyToOne
    @MapsId("shortyId")
    @JoinColumn(name = "shorty_id", referencedColumnName = "id")
    private Shorty shorty;

    private int counter = 0;
}
