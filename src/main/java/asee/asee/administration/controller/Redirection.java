package asee.asee.administration.controller;

import asee.asee.PraksaAseeApplication;
import asee.asee.administration.responseDtos.ResolvedHashResponse;
import asee.asee.administration.services.AuthenticationService;
import asee.asee.administration.services.ShortyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/")
public class Redirection {

    private static final Logger logger = LogManager.getLogger(PraksaAseeApplication.class);
    private final ShortyService shortyService;
    private final AuthenticationService authenticationService;

    @Autowired
    public Redirection(ShortyService shortyService, AuthenticationService authenticationService) {
        this.shortyService = shortyService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/{hash}")
    public ResponseEntity<HttpStatus> redirectUser(@PathVariable String hash) {
        logger.info("Redirecting user started for hash: {}", hash);

        HttpStatus httpStatus;
        ResolvedHashResponse serviceResponse;

        try{
            logger.info("Dohvaćam ulogiranog usera...");
            String loggedInUserAccountId = authenticationService.getLoggedInUsersAccountId();

            logger.info("Tražim vrijednost vaše rute...");
            serviceResponse =
                    shortyService.resolveTheHashedUrl(hash, loggedInUserAccountId);

            logger.info("Dohvaćam način prebacivanja...");
            httpStatus = serviceResponse.getRedirectionType() == 301
                    ? HttpStatus.MOVED_PERMANENTLY : HttpStatus.MOVED_TEMPORARILY;
        }catch (Exception e){

            logger.error("Došlo je do pogreške: {}", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        logger.info("Proslijeđujem vas na vašu lokaciju...");

        return ResponseEntity
                .status(httpStatus)
                .location(URI.create(serviceResponse.getUrl()))
                .build();
    }
}
