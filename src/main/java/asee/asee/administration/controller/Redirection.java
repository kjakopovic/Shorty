package asee.asee.administration.controller;

import asee.asee.administration.responseDtos.ResolvedHashResponse;
import asee.asee.administration.services.ShortyService;
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

    private final ShortyService shortyService;

    @Autowired
    public Redirection(ShortyService shortyService) {
        this.shortyService = shortyService;
    }

    @GetMapping("/{hash}")
    public ResponseEntity<HttpStatus> redirectUser(@PathVariable String hash) {
        HttpStatus httpStatus;
        ResolvedHashResponse serviceResponse;

        try{
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            serviceResponse =
                    shortyService.resolveTheHashedUrl(hash, authentication.getName());

            httpStatus = serviceResponse.getRedirectionType() == 301
                    ? HttpStatus.MOVED_PERMANENTLY : HttpStatus.MOVED_TEMPORARILY;
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ResponseEntity
                .status(httpStatus)
                .location(URI.create(serviceResponse.getUrl()))
                .build();
    }
}
