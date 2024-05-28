package asee.asee.administration.models;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class UserShortyId implements Serializable {

    private String userEntityId;
    private Integer shortyId;

    public UserShortyId() {}

    public UserShortyId(String userEntity, Integer shorty) {
        this.userEntityId = userEntity;
        this.shortyId = shorty;
    }
}
