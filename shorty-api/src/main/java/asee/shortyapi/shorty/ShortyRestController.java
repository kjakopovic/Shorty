package asee.shortyapi.shorty;

import asee.shortyapi.shorty.request.ShortyRequest;
import asee.shortyapi.shorty.response.ShortyResponse;
import asee.shortyapplication.authentication.interfaces.IAuthenticationService;
import asee.shortyapplication.shorty.dto.ResolvedHashResponse;
import asee.shortyapplication.shorty.interfaces.IShortyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ShortyRestController {
    public static final String HTTP_SHORTY_COM = "http://shorty.com/";

    private final IShortyService shortyService;
    private final IAuthenticationService authenticationService;

    @PostMapping("/short")
    public ResponseEntity<ShortyResponse> shortenTheUrl(@RequestBody ShortyRequest request) {
        var response = new ShortyResponse();

        try {
            shortyService.throwIfIncorrectRedirectionType(request.getRedirectType());

            var hashedUrl = shortyService.shortenTheUrl(
                    request.getUrl(),
                    request.getRedirectType(),
                    authenticationService.getLoggedInUsersAccountId());

            response.setShortUrl(HTTP_SHORTY_COM + hashedUrl); //pretpostavka da nam je to domena

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setDescription(e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Integer>> getUsersStatistics() {
        String loggedInUserAccountId = authenticationService.getLoggedInUsersAccountId();

        return ResponseEntity.ok(shortyService.getUsersShortyStatistics(loggedInUserAccountId));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<HttpStatus> redirectUser(@PathVariable String hash) {
        HttpStatus httpStatus;
        ResolvedHashResponse serviceResponse;

        try{
            var loggedInUserAccountId = authenticationService.getLoggedInUsersAccountId();

            serviceResponse =
                    shortyService.resolveTheHashedUrl(hash, loggedInUserAccountId);

            httpStatus = serviceResponse.getRedirectionType() == 301
                    ? HttpStatus.MOVED_PERMANENTLY : HttpStatus.MOVED_TEMPORARILY;
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        return ResponseEntity
                .status(httpStatus)
                .location(URI.create(serviceResponse.getUrl()))
                .build();
    }
}