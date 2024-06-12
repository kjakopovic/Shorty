package asee.asee.administration.services;

import asee.asee.PraksaAseeApplication;
import asee.asee.administration.models.Shorty;
import asee.asee.administration.models.UserEntity;
import asee.asee.administration.models.UserShorty;
import asee.asee.administration.models.UserShortyId;
import asee.asee.administration.repositories.IShortyRepository;
import asee.asee.administration.repositories.IUserRepository;
import asee.asee.administration.repositories.IUserShortyRepository;
import asee.asee.administration.responseDtos.ResolvedHashResponse;
import asee.asee.exceptions.ShortyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShortyService {

    private static final Logger logger = LogManager.getLogger(PraksaAseeApplication.class);
    private final PasswordEncoder passwordEncoder;
    private final IShortyRepository shortyRepository;
    private final IUserRepository userRepository;
    private final IUserShortyRepository userShortyRepository;

    @Autowired
    public ShortyService(PasswordEncoder passwordEncoder,
                         IShortyRepository shortyRepository,
                         IUserRepository userRepository,
                         IUserShortyRepository userShortyRepository) {
        this.passwordEncoder = passwordEncoder;
        this.shortyRepository = shortyRepository;
        this.userRepository = userRepository;
        this.userShortyRepository = userShortyRepository;
    }

    public String shortenTheUrl(String url, int redirectionType, String accountId) throws ShortyException {
        logger.info("Starting to shorten the url.");

        UserEntity loggedInUser;

        try {
            loggedInUser = userRepository.findById(accountId)
                    .orElseThrow();
        }catch (NoSuchElementException e){
            throw new ShortyException("Logged in user not found", e.getMessage());
        }

        try {
            logger.info("Check if original URL already exists");

            List<Shorty> shorty = shortyRepository.findShortiesByOriginalUrl(url);

            if (!shorty.isEmpty()){
                logger.info("Original URL exists.");

                logger.info("Checking if this user used this original URL before.");
                boolean isUserShortyExisting = userShortyRepository
                        .existsByUserEntityAccountIdAndShortyOriginalUrl(accountId, url);

                //ako konekcija za tog usera i shortija postoji ali je krivi redirection type
                // (jer jedan user ne moze imati vise redirection typeova na isti link)
                if (isUserShortyExisting && shorty.stream().anyMatch(s -> s.getRedirectionType() != redirectionType)){
                    logger.error("You already used this original URL with another redirection type!");
                    throw new Exception("Već ste napravili taj link sa drugim redirection typeom!");
                //ako konekcija postoji i tocan je redirection type
                }else if (isUserShortyExisting && shorty.stream().anyMatch(s -> s.getRedirectionType() == redirectionType)){
                    logger.info("Connection exists and redirection type is correct.");

                    Optional<Shorty> newShorty = shorty.stream()
                            .filter(s -> s.getRedirectionType() == redirectionType)
                            .findFirst();

                    if (newShorty.isPresent()){
                        logger.info("Returning shortened URL for this original URL when connection exists.");
                        return newShorty.get().getHashedUrl();
                    }
                //ako konekcija ne postoji, znaci da imamo zahtjev za novog usera za taj link
                }else {
                    logger.info("Connection doesn't exist. This is a request for the new connection!");
                    //ako je to taj shorty koji smo pronasli daj mu konekciju sa tim shortijem,
                    // inace ide kreiranje novog shortyja
                    if(shorty.stream().anyMatch(s -> s.getRedirectionType() == redirectionType)){
                        logger.info("Getting value from the already existing shorty.");
                        Optional<Shorty> newShorty = shorty.stream()
                                .filter(s -> s.getRedirectionType() == redirectionType)
                                .findFirst();

                        if (newShorty.isPresent()){
                            UserShorty userShorty = new UserShorty();
                            userShorty.setUserEntity(loggedInUser);
                            userShorty.setShorty(newShorty.get());
                            userShorty.setId(new UserShortyId(loggedInUser.getAccountId(), newShorty.get().getId()));

                            userShortyRepository.save(userShorty);

                            logger.info("Returning shortened URL for this original URL when connection doesn't exist.");
                            return newShorty.get().getHashedUrl();
                        }
                    }
                }
            }

            logger.info("Original URL doesn't exist already.");

            String hashedUrl = passwordEncoder.encode(url)
                    .replaceAll("[/:?&=#]", "X")
                    .substring(40, 45);

            logger.info("Making new shorty.");

            Shorty newShorty = new Shorty();
            newShorty.setOriginalUrl(url);
            newShorty.setHashedUrl(hashedUrl);
            newShorty.setRedirectionType(redirectionType);

            shortyRepository.save(newShorty);

            logger.info("Saving new user-shorty connection.");

            UserShorty userShorty = new UserShorty();
            userShorty.setUserEntity(loggedInUser);
            userShorty.setShorty(newShorty);
            userShorty.setId(new UserShortyId(loggedInUser.getAccountId(), newShorty.getId()));
            userShortyRepository.save(userShorty);

            return hashedUrl;
        }catch (Exception e) {
            logger.error("An error occurred while creating new Shorty: {}", e.getMessage());

            throw new ShortyException("New shorty couldn't be made", e.getMessage());
        }
    }

    public ResolvedHashResponse resolveTheHashedUrl(String hashedUrl, String accountId) throws ShortyException {
        logger.info("Received shortened URL.");

        Shorty shorty;
        UserShorty userShorty;

        try {
            logger.info("Trying to find the shorty.");
            shorty = shortyRepository.findByHashedUrl(hashedUrl)
                    .orElseThrow();
        }catch (NoSuchElementException e) {
            logger.error("Shorty was not found!");
            throw new ShortyException("No such shorty found", e.getMessage());
        }

        UserShortyId userShortyId = new UserShortyId(accountId, shorty.getId());

        try {
            logger.info("Trying to find the shorty connection to this user.");
            userShorty = userShortyRepository.findById(userShortyId)
                    .orElseThrow();
        }catch (NoSuchElementException e) {
            logger.error("User-shorty connection was not found!");
            throw new ShortyException("No such user-shorty found", e.getMessage());
        }

        logger.info("Increment call counter.");
        userShorty.setCounter(userShorty.getCounter() + 1);

        logger.info("Saving shorty updates.");
        shortyRepository.save(shorty);

        return new ResolvedHashResponse(shorty.getOriginalUrl(), shorty.getRedirectionType());
    }

    public Map<String, Integer> getUsersShortyStatistics(String accountId){
        logger.info("Starting to get users statistics.");
        Map<String, Integer> redirects = new HashMap<>();

        logger.info("Fetching shorties data.");
        List<UserShorty> shortiesData = userShortyRepository.findAllByUserEntityAccountIdWithShorty(accountId);

        logger.info("Mapping shorties data.");
        shortiesData.forEach(data -> {
            redirects.put(data.getShorty().getOriginalUrl(), data.getCounter());
        });

        logger.info("Returning statistics.");
        return redirects;
    }
}
