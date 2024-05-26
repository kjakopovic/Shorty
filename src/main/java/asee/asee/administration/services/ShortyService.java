package asee.asee.administration.services;

import asee.asee.administration.models.Shorty;
import asee.asee.administration.models.UserEntity;
import asee.asee.administration.repositories.IShortyRepository;
import asee.asee.administration.repositories.IUserRepository;
import asee.asee.administration.responseDtos.ResolvedHashResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShortyService {

    private final PasswordEncoder passwordEncoder;
    private final IShortyRepository shortyRepository;
    private final IUserRepository userRepository;

    @Autowired
    public ShortyService(PasswordEncoder passwordEncoder, IShortyRepository shortyRepository, IUserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.shortyRepository = shortyRepository;
        this.userRepository = userRepository;
    }

    public String shortenTheUrl(String url, int redirectionType, String accountId) {
        UserEntity loggedInUser = userRepository.findById(accountId)
                .orElseThrow();

        Optional<Shorty> shorty = shortyRepository.findByOriginalUrlAndRedirectionType(url, redirectionType);

        if (shorty.isPresent()){
            loggedInUser.getShortedUrls().add(shorty.get());
            userRepository.save(loggedInUser);

            return shorty.get().getHashedUrl();
        }

        String hashedUrl = passwordEncoder.encode(url).substring(0, 5);

        Shorty newShorty = new Shorty();
        newShorty.setOriginalUrl(url);
        newShorty.setHashedUrl(hashedUrl);
        newShorty.setRedirectionType(redirectionType);

        shortyRepository.save(newShorty);

        loggedInUser.getShortedUrls().add(newShorty);
        userRepository.save(loggedInUser);

        return hashedUrl;
    }

    public ResolvedHashResponse resolveTheHashedUrl(String hashedUrl) {
        Shorty shorty = shortyRepository.findByHashedUrl(hashedUrl)
                .orElseThrow();

        shorty.setTimesUsed(shorty.getTimesUsed() + 1);

        shortyRepository.save(shorty);

        return new ResolvedHashResponse(shorty.getOriginalUrl(), shorty.getRedirectionType());
    }
}
