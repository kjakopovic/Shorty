package asee.shortyapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"asee.shortyapplication", "asee.shortydb"})
public class ShortyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortyApplication.class, args);
    }

}
