package asee.asee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("asee.asee.administration.repositories")
@EntityScan("asee.asee.shortener.models")
public class PraksaAseeApplication {
    public static void main(String[] args) {
        SpringApplication.run(PraksaAseeApplication.class, args);
    }
}