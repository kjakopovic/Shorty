package asee.shortydb.postgres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"asee.shortydb"})
@EnableJpaRepositories(basePackages = "asee.shortydb")
@EntityScan(basePackages = "asee.shortydb")
public class ShortyDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortyDbApplication.class, args);
    }

}
