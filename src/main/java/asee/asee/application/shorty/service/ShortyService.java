package asee.asee.application.shorty.service;

import asee.asee.PraksaAseeApplication;
import asee.asee.application.authentification.dao.IUserDAO;
import asee.asee.application.authentification.model.UserModel;
import asee.asee.application.exceptions.ShortyException;
import asee.asee.application.shorty.dao.IShortyDAO;
import asee.asee.application.shorty.dao.IUserShortyDAO;
import asee.asee.application.shorty.dto.ResolvedHashResponse;
import asee.asee.application.shorty.model.ShortyModel;
import asee.asee.application.shorty.model.UserShortyModel;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class ShortyService {

    private static final Logger logger = LogManager.getLogger(PraksaAseeApplication.class);
    private final PasswordEncoder passwordEncoder;
    private final IShortyDAO shortyDao;
    private final IUserDAO userDao;
    private final IUserShortyDAO userShortyDao;

    @Transactional
    public String shortenTheUrl(String url, int redirectionType, String accountId) throws ShortyException {
        logger.info("Starting to shorten the url.");

        UserModel loggedInUser;

        try {
            loggedInUser = userDao.findById(accountId);
        }catch (NoSuchElementException e){
            throw new ShortyException("Logged in user not found", e.getMessage());
        }

        try {
            logger.info("Check if original URL already exists");

            var shorty = shortyDao.findShortiesByOriginalUrl(url);

            if (!shorty.isEmpty()){
                logger.info("Original URL exists.");

                logger.info("Checking if this user used this original URL before.");
                var isUserShortyExisting = userShortyDao
                        .existsByUserEntityAccountIdAndShortyOriginalUrl(accountId, url);

                //ako konekcija za tog usera i shortija postoji ali je krivi redirection type
                // (jer jedan user ne moze imati vise redirection typeova na isti link)
                if (isUserShortyExisting && shorty.stream().anyMatch(s -> s.getRedirectionType() != redirectionType)){
                    logger.error("You have already used this original URL with another redirection type!");
                    throw new ShortyException("You have already used this original URL with another redirection type!");
                //ako konekcija postoji i tocan je redirection type
                }else if (isUserShortyExisting && shorty.stream().anyMatch(s -> s.getRedirectionType() == redirectionType)){
                    logger.info("Connection exists and redirection type is correct.");

                    var newShorty = shorty.stream()
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
                        var newShorty = shorty.stream()
                                .filter(s -> s.getRedirectionType() == redirectionType)
                                .findFirst();

                        if (newShorty.isPresent()){
                            var userShortyModel = new UserShortyModel();
                            userShortyModel.setUser(loggedInUser);
                            userShortyModel.setShorty(newShorty.get());

                            userShortyDao.save(userShortyModel);

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

            var newShorty = new ShortyModel();
            newShorty.setOriginalUrl(url);
            newShorty.setHashedUrl(hashedUrl);
            newShorty.setRedirectionType(redirectionType);

            var newShortyId = shortyDao.save(newShorty);
            newShorty.setId(newShortyId);

            logger.info("Saving new user-shorty connection.");

            var userShortyModel = new UserShortyModel();
            userShortyModel.setUser(loggedInUser);
            userShortyModel.setShorty(newShorty);

            userShortyDao.save(userShortyModel);

            return hashedUrl;
        }catch (Exception e) {
            logger.error("An error occurred while creating new Shorty: {}", e.getMessage());

            throw new ShortyException("New shorty couldn't be made", e.getMessage());
        }
    }

    @Transactional
    public ResolvedHashResponse resolveTheHashedUrl(String hashedUrl, String accountId) throws ShortyException {
        logger.info("Received shortened URL.");

        ShortyModel shorty;
        UserShortyModel userShorty;

        try {
            logger.info("Trying to find the shorty.");
            shorty = shortyDao.findByHashedUrl(hashedUrl);
        }catch (NoSuchElementException e) {
            logger.error("Shorty was not found!");
            throw new ShortyException("No such shorty found", e.getMessage());
        }

        try {
            logger.info("Trying to find the shorty connection to this user.");
            userShorty = userShortyDao.findByUserShortyId(accountId, shorty.getId());
        }catch (NoSuchElementException e) {
            logger.error("User-shorty connection was not found!");
            throw new ShortyException("No such user-shorty found", e.getMessage());
        }

        logger.info("Increment call counter.");
        userShorty.setCounter(userShorty.getCounter() + 1);

        logger.info("Saving user-shorty updates.");
        userShortyDao.save(userShorty);

        return new ResolvedHashResponse(shorty.getOriginalUrl(), shorty.getRedirectionType());
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getUsersShortyStatistics(String accountId){
        logger.info("Starting to get users statistics.");
        Map<String, Integer> redirects = new HashMap<>();

        logger.info("Fetching shorties data.");
        var shortiesData = userShortyDao.findAllByUserEntityAccountIdWithShorty(accountId);

        logger.info("Mapping shorties data.");
        shortiesData.forEach(data -> {
            redirects.put(data.getShorty().getOriginalUrl(), data.getCounter());
        });

        logger.info("Returning statistics.");
        return redirects;
    }

    public void throwIfIncorrectRedirectionType(Integer redirectionType) throws ShortyException {
        if (redirectionType != 301 && redirectionType != 302) {
            throw new ShortyException("Please enter redirection type 301 or 302!");
        }
    }
}
