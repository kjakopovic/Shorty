package asee.shortyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"asee.shortyapplication", "asee.shortyapi"})
public class ShortyApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortyApiApplication.class, args);
    }

}
