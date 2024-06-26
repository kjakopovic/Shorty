package asee.asee.application.shorty.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class ShortyModel {
    private Integer id;

    @Length(max = 200)
    private String originalUrl;

    @Length(max = 5)
    private String hashedUrl;

    private int redirectionType;
}
