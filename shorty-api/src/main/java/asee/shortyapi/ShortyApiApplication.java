package asee.shortyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// TODO: napisati dodatne testove za core modele i db repozitorije
// TODO: rijesi docker i CI pipeline

@SpringBootApplication
@ComponentScan(basePackages = {"asee.shortyapplication", "asee.shortyapi"})
public class ShortyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortyApiApplication.class, args);
    }

}
