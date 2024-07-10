package asee.shortyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"asee.shortyapplication", "asee.shortyapi"})
public class ShortyApiApplication {
    //TODO: pogledaj radi li rola za swagger
    //TODO: napravi u docker-compose runnanje docker imagea za keycloak i provjeri radi li sve kako treba kroz docker
    public static void main(String[] args) {
        SpringApplication.run(ShortyApiApplication.class, args);
    }

}
