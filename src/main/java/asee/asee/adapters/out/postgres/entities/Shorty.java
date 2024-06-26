package asee.asee.adapters.out.postgres.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shorties")
@Getter
@Setter
public class Shorty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Length(max = 200)
    private String originalUrl;

    @Length(max = 5)
    private String hashedUrl;

    private int redirectionType;

    @OneToMany(mappedBy = "shorty", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserShorty> shortedUrls = new ArrayList<>();
}
