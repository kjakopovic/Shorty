package asee.asee.administration.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "shorties")
@Data
public class Shorty {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Length(max = 200)
    private String originalUrl;

    @Length(max = 5)
    private String hashedUrl;

    private int redirectionType;

    private int timesUsed = 0;
}
